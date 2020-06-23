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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.FluentMap;
import org.springframework.mock.env.MockEnvironment;

import java.nio.file.Paths;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.VaultBindingsPropertiesProcessor.KIND;

@DisplayName("Vault BindingsPropertiesProcessor")
final class VaultPropertiesProcessorTest {

    private FluentMap baseSecret() {
        return new FluentMap()
                .withEntry("uri", "test-uri")
                .withEntry("namespace", "test-namespace");
    }

    private FluentMap metadata() {
        return new FluentMap()
                .withEntry("kind", KIND);
    }

    private final Binding tokenBinding = new Binding(
            "test-name", Paths.get("test-path"),
            metadata(),
            baseSecret()
                    .withEntry("method", "token")
                    .withEntry("token", "test-token")
    );

    private final Binding appRoleBinding = new Binding(
            "test-name", Paths.get("test-path"),
            metadata(),
            baseSecret()
                    .withEntry("method", "approle")
                    .withEntry("role-id", "test-role-id")
                    .withEntry("secret-id", "test-secret-id")
                    .withEntry("role", "test-role")
                    .withEntry("app-role-path", "test-app-role-path")
    );

    private final Binding cubbyholeBinding = new Binding(
            "test-name", Paths.get("test-path"),
            metadata(),
            baseSecret()
                    .withEntry("method", "cubbyhole")
                    .withEntry("token", "test-token")
    );

    private final Binding certBinding = new Binding(
            "test-name", Paths.get("test-path"),
            metadata(),
            baseSecret()
                    .withEntry("method", "cert")
                    .withEntry("keystore.jks", "key store contents!")
                    .withEntry("key-store-password", "test-key-store-password")
                    .withEntry("cert-auth-path", "test-cert-auth-path")
    );

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("Supports token authentication")
    void testTokenAuthentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(tokenBinding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "TOKEN")
                .containsEntry("spring.cloud.vault.token", "test-token");
    }

    @Test
    @DisplayName("Supports AppRole authentication")
    void testAppRoleAuthentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(appRoleBinding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "APPROLE")
                .containsEntry("spring.cloud.vault.app-role.role-id", "test-role-id")
                .containsEntry("spring.cloud.vault.app-role.secret-id", "test-secret-id")
                .containsEntry("spring.cloud.vault.app-role.role", "test-role")
                .containsEntry("spring.cloud.vault.app-role.app-role-path", "test-app-role-path");
    }

    @Test
    @DisplayName("Supports cubbyhole authentication")
    void testCubbyholeAuthentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(cubbyholeBinding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "CUBBYHOLE")
                .containsEntry("spring.cloud.vault.token", "test-token");
    }

    @Test
    @DisplayName("Supports TLS certificate authentication")
    void testCertAuthentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(certBinding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "CERT")
                .containsEntry("spring.cloud.vault.ssl.key-store", "test-path/secret/keystore.jks")
                .containsEntry("spring.cloud.vault.ssl.key-store-password", "test-key-store-password")
                .containsEntry("spring.cloud.vault.ssl.cert-auth-path", "test-cert-auth-path");
    }

    @Test
    @DisplayName("Doesn't fail when method is missing")
    void testMissingProvider() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(new Binding(
                "test-name",
                Paths.get("test-path"),
                metadata(),
                baseSecret()
        )), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .doesNotContainKey("spring.cloud.vault.authentication");
    }

    //Vault agent authentication can be configured using a sidecar and should not require a binding

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.vault.enable", "false");

        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(
                tokenBinding,
                appRoleBinding,
                cubbyholeBinding,
                certBinding
        ), properties);

        assertThat(properties).isEmpty();
    }

}
