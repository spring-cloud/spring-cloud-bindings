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
import org.springframework.core.env.Environment;
import org.springframework.cloud.bindings.boot.pem.PemSslStoreHelper;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Map;
import java.util.Random;

import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of type: {@value TYPE}.
 */
final class EurekaBindingsPropertiesProcessor implements BindingsPropertiesProcessor {
    /**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "eureka";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isTypeEnabled(environment, TYPE)) {
            return;
        }

        bindings.filterBindings(TYPE).forEach(binding -> {
            Map<String, String> secret = binding.getSecret();
            MapMapper map = new MapMapper(secret, properties);

            map.from("client-id").to("eureka.client.oauth2.client-id");
            map.from("access-token-uri").to("eureka.client.oauth2.access-token-uri");
            map.from("uri").to("eureka.client.serviceUrl.defaultZone",
                    (uri) -> String.format("%s/eureka/", uri)
            );
            properties.put("eureka.client.region", "default");

            String caCert = secret.get("ca.crt");
            if (caCert != null && !caCert.isEmpty()) {
                // generally apps using TLS bindings will be running in k8s where the host name is not meaningful,
                // but we don't want to override the endpoint behavior the app has already set, in case they want to
                // explicitly set eureka.instance.hostname to route traffic through normal ingress.
                if (! environment.containsProperty("eureka.instance.preferIpAddress")) {
                    properties.put("eureka.instance.preferIpAddress", true);
                }

                Random random = new Random();
                String generatedPassword = random.ints(97 /* letter a */, 122 /* letter z */ + 1)
                        .limit(10)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();

                // Create a trust store from the CA cert
                String trustFilePath = Paths.get(System.getProperty("java.io.tmpdir"), "client-truststore.p12").toString();
                KeyStore trustStore = PemSslStoreHelper.createKeyStore("trust", "PKCS12", caCert, null, "rootca");
                createStoreFile("truststore", generatedPassword, trustFilePath, trustStore);
                properties.put("eureka.client.tls.enabled", true);
                properties.put("eureka.client.tls.trust-store", "file:"+trustFilePath);
                properties.put("eureka.client.tls.trust-store-type", "PKCS12");
                properties.put("eureka.client.tls.trust-store-password", generatedPassword);

                // When tls.crt and tls.key are set, enable mTLS for Eureka
                String clientKey = secret.get("tls.key");
                String clientCert = secret.get("tls.crt");
                if (StringUtils.hasText(clientCert) != StringUtils.hasText(clientKey)) {
                    throw new IllegalArgumentException("binding secret error: tls.key and tls.crt must both be set if either is set");
                }
                if (clientKey != null && !clientKey.isEmpty()) {

                    // Create a keystore
                    String keyFilePath = Paths.get(System.getProperty("java.io.tmpdir"), "client-keystore.p12").toString();
                    KeyStore keyStore = PemSslStoreHelper.createKeyStore("key", "PKCS12", clientCert, clientKey, "eureka");
                    createStoreFile("keystore", generatedPassword, keyFilePath, keyStore);
                    properties.put("eureka.client.tls.key-alias", "eureka");
                    properties.put("eureka.client.tls.key-store", "file:" + keyFilePath);
                    properties.put("eureka.client.tls.key-store-type", "PKCS12");
                    properties.put("eureka.client.tls.key-store-password", generatedPassword);
                    properties.put("eureka.client.tls.key-password", "");
                }
            }
        });
    }

    private static void createStoreFile(String storeType, String generatedPassword, String filePath, KeyStore ks) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            try {
                ks.store(fos, generatedPassword.toCharArray());
            } catch (KeyStoreException e) {
                throw new IllegalStateException("Unable to write " + storeType, e);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("Cryptographic algorithm not available", e);
            } catch (CertificateException e) {
                throw new IllegalStateException("Unable to process certificate", e);
            } catch (IOException e) {
                throw new IllegalStateException("Unable to create " + storeType, e);
            } finally {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to close " + storeType + " output file", e);
                }
            }
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Unable to open " + storeType + " output file", e);
        }
    }
}
