/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.bindings.boot;

import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.boot.pem.PemSslStoreHelper;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Random;

import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of type: {@value TYPE}.
 */
final class ConfigServerBindingsPropertiesProcessor implements BindingsPropertiesProcessor {
    /**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "config";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isTypeEnabled(environment, TYPE)) {
            return;
        }

        bindings.filterBindings(TYPE).forEach(binding -> {
            Map<String, String> secret = binding.getSecret();
            MapMapper map = new MapMapper(secret, properties);
            map.from("uri").to("spring.cloud.config.uri");
            map.from("client-id").to("spring.cloud.config.client.oauth2.clientId");
            map.from("client-secret").to("spring.cloud.config.client.oauth2.clientSecret");
            map.from("access-token-uri").to("spring.cloud.config.client.oauth2.accessTokenUri");

            // When tls.crt and tls.key are set, enable mTLS for config client.
            String clientKey = secret.get("tls.key");
            String clientCert = secret.get("tls.crt");
            if (StringUtils.hasText(clientCert) != StringUtils.hasText(clientKey)) {
                throw new IllegalArgumentException("binding secret error: tls.key and tls.crt must both be set if either is set");
            }

            if (clientKey != null && !clientKey.isEmpty()) {
                String generatedPassword = new Random().ints(97 /* letter a */, 122 /* letter z */ + 1)
                        .limit(10)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();

                // Create a keystore
                String keyFilePath = createStore("client-keystore", generatedPassword, clientCert, clientKey, "config");

                properties.put("spring.cloud.config.tls.enabled", true);
                properties.put("spring.cloud.config.tls.key-alias", "config");
                properties.put("spring.cloud.config.tls.key-store", "file:" + keyFilePath);
                properties.put("spring.cloud.config.tls.key-store-type", "PKCS12");
                properties.put("spring.cloud.config.tls.key-store-password", generatedPassword);
                properties.put("spring.cloud.config.tls.key-password", "");

                String caCert = secret.get("ca.crt");
                if (caCert != null && !caCert.isEmpty()) {
                    // Create a truststore from the CA cert
                    String trustFilePath = createStore("client-truststore", generatedPassword, caCert, null, "ca");
                    properties.put("spring.cloud.config.tls.trust-store", "file:" + trustFilePath);
                    properties.put("spring.cloud.config.tls.trust-store-type", "PKCS12");
                    properties.put("spring.cloud.config.tls.trust-store-password", generatedPassword);
                }
            }
        });
    }

    private static String createStore(String name, String password, String certificate, String privateKey, String keyAlias) {
        String path = Paths.get(System.getProperty("java.io.tmpdir"), name + ".p12").toString();
        KeyStore store = PemSslStoreHelper.createKeyStore("key", "PKCS12", certificate, privateKey, keyAlias);

        try (FileOutputStream fos = new FileOutputStream(path)) {
            store.store(fos, password.toCharArray());
        } catch (KeyStoreException e) {
            throw new IllegalStateException("Unable to write " + name, e);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Cryptographic algorithm not available", e);
        } catch (CertificateException e) {
            throw new IllegalStateException("Unable to process certificate", e);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to create " + name, e);
        }
        return path;
    }
}
