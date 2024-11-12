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
import static org.springframework.cloud.bindings.boot.EurekaBindingsPropertiesProcessor.TYPE;

@DisplayName("Eureka BindingsPropertiesProcessor")
final class EurekaBindingsPropertiesProcessorTest {
    private Bindings bindings = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry(Binding.TYPE, TYPE)
                            .withEntry("uri", "https://test-uri")
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
    @DisplayName("contributes only base properties when no auth is set")
    void testNoAuth() {
        new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties)
                .containsExactlyInAnyOrderEntriesOf(new FluentMap()
                        .withEntry("eureka.client.region", "default")
                        .withEntry("eureka.client.serviceUrl.defaultZone", "https://test-uri/eureka/")
                        .withEntry("spring.cloud.loadbalancer.configurations", "zone-preference")
                        .withEntry("eureka.instance.metadata-map.zone", "test-uri"));
    }

    @Test
    @DisplayName("contributes oauth properties when set")
    void testOAuth2() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, TYPE)
                                .withEntry("uri", "test-uri")
                                .withEntry("client-id", "test-client-id")
                                .withEntry("client-secret", "test-client-secret")
                                .withEntry("access-token-uri", "test-access-token-uri")
                )
        );

        new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties)
                .containsEntry("eureka.client.region", "default")
                .containsEntry("eureka.client.oauth2.client-id", "test-client-id")
                .containsEntry("eureka.client.oauth2.access-token-uri", "test-access-token-uri")
                .containsEntry("eureka.client.serviceUrl.defaultZone", "test-uri/eureka/");
    }

    @Test
    @DisplayName("contributes tls properties when set")
    void testTls() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, TYPE)
                                .withEntry("uri", "test-uri")
                                .withEntry("ca.crt", cert)
                )
        );

        new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties)
                .containsEntry("eureka.client.region", "default")
                .containsKey("eureka.client.tls.trust-store")
                .containsEntry("eureka.client.tls.trust-store-type", "PKCS12")
                .containsKey("eureka.client.tls.trust-store-password");
        assertDoesNotThrow(() -> {
            String path = properties.get("eureka.client.tls.trust-store").toString().substring(5);
            File f = new File(path);
            assertThat(f.isFile()).isTrue();
            f.delete();
        });
    }

    @Test
    @DisplayName("throws when bad tls values are set")
    void testBadTls() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, TYPE)
                                .withEntry("uri", "test-uri")
                                .withEntry("ca.crt", "this isn't a valid certificate")
                )
        );

        assertThrows(IllegalStateException.class, () -> {
            new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);
        });
    }

    @Test
    @DisplayName("does not change PreferIpAddress if already set elsewhere")
    void testTlsNoIp() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, TYPE)
                                .withEntry("uri", "test-uri")
                                .withEntry("ca.crt", cert)
                )
        );
        environment.setProperty("eureka.instance.preferIpAddress", "false");

        new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).doesNotContainKey("eureka.instance.preferIpAddress");
        assertDoesNotThrow(() -> {
            String path = properties.get("eureka.client.tls.trust-store").toString().substring(5);
            File f = new File(path);
            assertThat(f.isFile()).isTrue();
            f.delete();
        });
    }

    @Test
    @DisplayName("contributes mTLS properties when set")
    void testMtls() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, TYPE)
                                .withEntry("uri", "test-uri")
                                .withEntry("ca.crt", cert)
                                .withEntry("tls.crt", cert)
                                .withEntry("tls.key", key)
                )
        );

        new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties)
                .containsEntry("eureka.client.region", "default")
                .containsKey("eureka.client.tls.trust-store")
                .containsEntry("eureka.client.tls.trust-store-type", "PKCS12")
                .containsKey("eureka.client.tls.trust-store-password")
                .containsEntry("eureka.client.tls.key-alias", "eureka")
                .containsKey("eureka.client.tls.key-store")
                .containsEntry("eureka.client.tls.key-store-type", "PKCS12")
                .containsKey("eureka.client.tls.key-store-password")
                .containsEntry("eureka.client.tls.key-password", "");

        assertDoesNotThrow(() -> {
            String path = properties.get("eureka.client.tls.key-store").toString().substring(5);
            File f = new File(path);
            assertThat(f.isFile()).isTrue();
            f.delete();
            path = properties.get("eureka.client.tls.trust-store").toString().substring(5);
            f = new File(path);
            assertThat(f.isFile()).isTrue();
            f.delete();
        });
    }

    @Test
    @DisplayName("throws when bad mTls values are set")
    void testBadMtls() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, TYPE)
                                .withEntry("uri", "test-uri")
                                .withEntry("ca.crt", cert)
                                .withEntry("tls.crt", "this is not a valid certificate")
                                .withEntry("tls.key", "this is not a valid key")
                )
        );

        assertThrows(IllegalStateException.class, () -> {
            new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);
        });
    }

    @Test
    @DisplayName("throws when tls.crt is set but tls.key isn't")
    void testNoTlsKey() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, TYPE)
                                .withEntry("uri", "test-uri")
                                .withEntry("ca.crt", cert)
                                .withEntry("tls.crt", cert)
                )
        );

        assertThrows(IllegalArgumentException.class, () -> {
            new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);
        });
    }

    @Test
    @DisplayName("throws when tls.key is set but tls.crt isn't")
    void testNoTlsCrt() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, TYPE)
                                .withEntry("uri", "test-uri")
                                .withEntry("ca.crt", cert)
                                .withEntry("tls.key", key)
                )
        );

        assertThrows(IllegalArgumentException.class, () -> {
            new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);
        });
    }

    @Test
    @DisplayName("handles eureka zone for uri without scheme")
    void zoneFromUriWithoutScheme() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, TYPE)
                                .withEntry("uri", "test-uri")
                )
        );
        new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties)
                .containsExactlyInAnyOrderEntriesOf(new FluentMap()
                        .withEntry("eureka.client.region", "default")
                        .withEntry("eureka.client.serviceUrl.defaultZone", "test-uri/eureka/")
                        .withEntry("spring.cloud.loadbalancer.configurations", "zone-preference")
                        .withEntry("eureka.instance.metadata-map.zone", "test-uri")
                );
    }

    @Test
    @DisplayName("handles eureka zone for malformed uri")
    void zoneFromMalformedUri() {
        bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        new FluentMap()
                                .withEntry(Binding.TYPE, TYPE)
                                .withEntry("uri", "http:")
                )
        );
        new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties)
                .containsExactlyInAnyOrderEntriesOf(new FluentMap()
                        .withEntry("eureka.client.region", "default")
                        .withEntry("eureka.client.serviceUrl.defaultZone", "http:/eureka/")
                        .withEntry("spring.cloud.loadbalancer.configurations", "zone-preference")
                        .withEntry("eureka.instance.metadata-map.zone", "")
                );
    }

    @Test
    @DisplayName("prefers ip address in kubernetes")
    void preferIpAddressInKubernetes() {
        environment.setProperty("spring.main.cloud-platform", "kubernetes");

        new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).containsEntry("eureka.instance.preferIpAddress", true);
    }

    @Test
    @DisplayName("prefers ip address in kubernetes")
    void doesNotOverridePreferIpAddressInKubernetes() {
        environment.setProperty("eureka.instance.preferIpAddress", "false");
        environment.setProperty("spring.main.cloud-platform", "kubernetes");

        new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).doesNotContainKey("eureka.instance.preferIpAddress");
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.eureka.enable", "false");

        new KafkaBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }
}
