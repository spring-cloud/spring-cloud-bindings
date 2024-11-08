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

import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.core.env.Environment;
import org.springframework.cloud.bindings.boot.pem.PemSslStoreHelper;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

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
            map.from("uri").to("eureka.instance.metadata-map.zone",
                    this::hostnameFromUri
            );
            properties.put("eureka.client.region", "default");
            properties.put("spring.cloud.loadbalancer.configurations", "zone-preference");


            if (isKubernetesPlatform(environment)) {
                // generally for apps running in k8s hostname is not meaningful,
                // but we don't want to override the endpoint behavior the app has already set, in case they want to
                // explicitly set eureka.instance.hostname to route traffic through normal ingress.
                if (!environment.containsProperty("eureka.instance.preferIpAddress")) {
                    properties.put("eureka.instance.preferIpAddress", true);
                }
            }

            String caCert = secret.get("ca.crt");
            if (caCert != null && !caCert.isEmpty()) {
                String generatedPassword = PemSslStoreHelper.generatePassword();

                // Create a trust store from the CA cert
                Path trustFilePath = PemSslStoreHelper.createKeyStoreFile("eureka-truststore", generatedPassword, caCert, null, "rootca");

                properties.put("eureka.client.tls.enabled", true);
                properties.put("eureka.client.tls.trust-store", "file:" + trustFilePath);
                properties.put("eureka.client.tls.trust-store-type", PemSslStoreHelper.PKCS12_STORY_TYPE);
                properties.put("eureka.client.tls.trust-store-password", generatedPassword);

                // When tls.crt and tls.key are set, enable mTLS for Eureka
                String clientKey = secret.get("tls.key");
                String clientCert = secret.get("tls.crt");
                if (StringUtils.hasText(clientCert) != StringUtils.hasText(clientKey)) {
                    throw new IllegalArgumentException("binding secret error: tls.key and tls.crt must both be set if either is set");
                }
                if (clientKey != null && !clientKey.isEmpty()) {

                    // Create a keystore
                    Path keyFilePath = PemSslStoreHelper.createKeyStoreFile("eureka-keystore", generatedPassword, clientCert, clientKey, "eureka");
                    properties.put("eureka.client.tls.key-alias", "eureka");
                    properties.put("eureka.client.tls.key-store", "file:" + keyFilePath);
                    properties.put("eureka.client.tls.key-store-type", PemSslStoreHelper.PKCS12_STORY_TYPE);
                    properties.put("eureka.client.tls.key-store-password", generatedPassword);
                    properties.put("eureka.client.tls.key-password", "");
                }
            }
        });
    }

    private boolean isKubernetesPlatform(Environment environment) {
        return CloudPlatform.KUBERNETES == CloudPlatform.getActive(environment);
    }

    private String hostnameFromUri(String uri) {
        if (!StringUtils.hasText(uri)) {
            return "";
        }

        try {
            URI u = URI.create(uri);
            if (u.getHost() != null) {
                return u.getHost();
            }
            if (u.getScheme() == null) {
                return URI.create("ignore://" + uri).getHost();
            }
        } catch (IllegalArgumentException e) {
            //ignore malformed uri
        }
        return "";
    }
}
