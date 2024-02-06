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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.FluentMap;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.env.MockEnvironment;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.cloud.bindings.boot.ConfigServerBindingsPropertiesProcessor.TYPE;

@DisplayName("Config Server BindingsPropertiesProcessor")
final class ConfigServerBindingsPropertiesProcessorTest {

    private Bindings bindings = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry(Binding.TYPE, TYPE)
                            .withEntry("uri", "test-uri")
                            .withEntry("client-id", "test-client-id")
                            .withEntry("client-secret", "test-client-secret")
                            .withEntry("access-token-uri", "test-access-token-uri")
            )
    );

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    private String cert;
    private String key;

    @BeforeEach
    void fetchCerts() {
        assertDoesNotThrow(() -> {
            this.cert = TestHelper.resourceAsString(new ClassPathResource("pem/test-cert.pem"));
            this.key = TestHelper.resourceAsString(new ClassPathResource("pem/test-key.pem"));
        });
    }

    @Test
    @DisplayName("contributes properties")
    void whenEnabled() {
        new ConfigServerBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.cloud.config.uri", "test-uri")
                .containsEntry("spring.cloud.config.client.oauth2.clientId", "test-client-id")
                .containsEntry("spring.cloud.config.client.oauth2.clientSecret", "test-client-secret")
                .containsEntry("spring.cloud.config.client.oauth2.accessTokenUri", "test-access-token-uri");
    }

    @Test
    @DisplayName("can be disabled")
    void whenDisabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.config.enable", "false");

        new ConfigServerBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }

    @Test
    @DisplayName("contributes tls key-store properties when set")
    void whenKeystoreValuesSet() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, ConfigServerBindingsPropertiesProcessor.TYPE)
                                .withEntry("tls.key", key)
                                .withEntry("tls.crt", cert)
                )
        );

        new ConfigServerBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties)
                .containsEntry("spring.cloud.config.tls.enabled", true)
                .containsEntry("spring.cloud.config.tls.key-store-type", "PKCS12")
                .containsEntry("spring.cloud.config.tls.key-alias", "config")
                .containsKey("spring.cloud.config.tls.key-store")
                .containsKey("spring.cloud.config.tls.key-store-password")
                .containsKey("spring.cloud.config.tls.key-password")
                .doesNotContainKey("spring.cloud.config.tls.trust-store")
                .doesNotContainKey("spring.cloud.config.tls.trust-store-type")
                .doesNotContainKey("spring.cloud.config.tls.trust-store-password");

        String path = properties.get("spring.cloud.config.tls.key-store").toString().substring(5);
        File f = new File(path);
        assertThat(f.exists()).isTrue();
        assertThat(f.isFile()).isTrue();
    }

    @Test
    @DisplayName("contributes tls trust-store properties when set")
    void whenTruststoreValuesSet() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, ConfigServerBindingsPropertiesProcessor.TYPE)
                                .withEntry("tls.key", key)
                                .withEntry("tls.crt", cert)
                                .withEntry("ca.crt", cert)
                )
        );

        new ConfigServerBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties)
                .containsEntry("spring.cloud.config.tls.enabled", true)
                .containsEntry("spring.cloud.config.tls.trust-store-type", "PKCS12")
                .containsKey("spring.cloud.config.tls.trust-store")
                .containsKey("spring.cloud.config.tls.trust-store-password");

        String path = properties.get("spring.cloud.config.tls.trust-store").toString().substring(5);
        File f = new File(path);
        assertThat(f.exists()).isTrue();
        assertThat(f.isFile()).isTrue();
    }

    @Test
    @DisplayName("throws when bad tls key-store values are set")
    void whenKeystoreValueIsNotValid() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, ConfigServerBindingsPropertiesProcessor.TYPE)
                                .withEntry("tls.key", key)
                                .withEntry("tls.crt", "this isn't a valid certificate")
                )
        );

        assertThrows(IllegalStateException.class, () -> {
            new ConfigServerBindingsPropertiesProcessor().process(environment, bindings, properties);
        });
    }

    @Test
    @DisplayName("throws when bad tls trust-store values are set")
    void whenTruststoreValueIsNotValid() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, ConfigServerBindingsPropertiesProcessor.TYPE)
                                .withEntry("tls.key", key)
                                .withEntry("tls.crt", cert)
                                .withEntry("ca.crt", "this isn't a valid certificate")
                )
        );

        assertThrows(IllegalStateException.class, () -> {
            new ConfigServerBindingsPropertiesProcessor().process(environment, bindings, properties);
        });
    }

    @Test
    @DisplayName("throws when tls.crt is set but tls.key isn't")
    void whenCertificateSetWithoutPrivateKey() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, ConfigServerBindingsPropertiesProcessor.TYPE)
                                .withEntry("tls.crt", cert)
                )
        );

        assertThrows(IllegalArgumentException.class, () -> {
            new ConfigServerBindingsPropertiesProcessor().process(environment, bindings, properties);
        });
    }

    @Test
    @DisplayName("throws when tls.key is set but tls.crt isn't")
    void whenPrivateKeySetWithoutCertificate() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, ConfigServerBindingsPropertiesProcessor.TYPE)
                                .withEntry("tls.key", key)
                )
        );

        assertThrows(IllegalArgumentException.class, () -> {
            new ConfigServerBindingsPropertiesProcessor().process(environment, bindings, properties);
        });
    }
}
