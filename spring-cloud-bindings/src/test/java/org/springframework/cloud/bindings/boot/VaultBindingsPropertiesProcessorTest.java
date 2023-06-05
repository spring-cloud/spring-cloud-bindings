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
import static org.springframework.cloud.bindings.boot.VaultBindingsPropertiesProcessor.TYPE;

@DisplayName("Vault BindingsPropertiesProcessor")
final class VaultBindingsPropertiesProcessorTest {

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("Supports token authentication")
    void testTokenAuthentication() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "token")
                                .withEntry("token", "test-token")
                )
        );

    }

    @Test
    @DisplayName("Supports AppRole authentication")
    void testAppRoleAuthentication() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("app-role-path", "test-app-role-path")
                                .withEntry("authentication-method", "approle")
                                .withEntry("role", "test-role")
                                .withEntry("role-id", "test-role-id")
                                .withEntry("secret-id", "test-secret-id")
                )
        );

        new VaultBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "approle")
                .containsEntry("spring.cloud.vault.app-role.role-id", "test-role-id")
                .containsEntry("spring.cloud.vault.app-role.secret-id", "test-secret-id")
                .containsEntry("spring.cloud.vault.app-role.role", "test-role")
                .containsEntry("spring.cloud.vault.app-role.app-role-path", "test-app-role-path");
    }

    @Test
    @DisplayName("Supports cubbyhole authentication")
    void testCubbyholeAuthentication() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "cubbyhole")
                                .withEntry("token", "test-token")
                )
        );

        new VaultBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "cubbyhole")
                .containsEntry("spring.cloud.vault.token", "test-token");
    }

    @Test
    @DisplayName("Supports TLS certificate authentication")
    void testCertAuthentication() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "cert")
                                .withEntry("cert-auth-path", "test-cert-auth-path")
                                .withEntry("key-store-password", "test-key-store-password")
                                .withEntry("truststore.jks", "trust store contents!")
                                .withEntry("trust-store-password", "test-trust-store-password")
                                .withEntry("keystore.jks", "key store contents!")
                )
        );

        new VaultBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "cert")
                .containsEntry("spring.cloud.vault.ssl.cert-auth-path", "test-cert-auth-path")
                .containsEntry("spring.cloud.vault.ssl.key-store", "test-path/keystore.jks")
                .containsEntry("spring.cloud.vault.ssl.key-store-password", "test-key-store-password")
                .containsEntry("spring.cloud.vault.ssl.trust-store", "test-path/truststore.jks")
                .containsEntry("spring.cloud.vault.ssl.trust-store-password", "test-trust-store-password");
    }

    @Test
    @DisplayName("Supports AWS EC2 authentication")
    void testAwsEc2Authentication() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "aws_ec2")
                                .withEntry("aws-ec2-instance-identity-document", "test-identity-document")
                                .withEntry("nonce", "test-nonce")
                                .withEntry("aws-ec2-path", "test-aws-ec2-path")
                                .withEntry("role", "test-role")
                )
        );

    }

    @Test
    @DisplayName("Supports AWS IAM authentication")
    void testAwsIamAuthentication() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "aws_iam")
                                .withEntry("aws-iam-server-id", "test-server-id")
                                .withEntry("aws-iam-server-name", "test-server-name")
                                .withEntry("aws-path", "test-aws-path")
                                .withEntry("aws-sts-endpoint-uri", "test-endpoint-uri")
                                .withEntry("role", "test-role")
                )
        );

        new VaultBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "aws_iam")
                .containsEntry("spring.cloud.vault.aws-iam.role", "test-role")
                .containsEntry("spring.cloud.vault.aws-iam.aws-path", "test-aws-path")
                .containsEntry("spring.cloud.vault.aws-iam.server-name", "test-server-name")
                .containsEntry("spring.cloud.vault.aws-iam.endpoint-uri", "test-endpoint-uri");
    }

    @Test
    @DisplayName("Supports Azure MSI authentication")
    void testAzureMsiAuthentication() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "azure_msi")
                                .withEntry("azure-path", "test-azure-path")
                                .withEntry("role", "test-role")
                                .withEntry("metadata-service", "test-metadata-service")
                                .withEntry("identity-token-service", "test-identity-token-service")
                )
        );

        new VaultBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "azure_msi")
                .containsEntry("spring.cloud.vault.azure-msi.role", "test-role")
                .containsEntry("spring.cloud.vault.azure-msi.azure-path", "test-azure-path")
                .containsEntry("spring.cloud.vault.azure-msi.metadata-service", "test-metadata-service")
                .containsEntry("spring.cloud.vault.azure-msi.identity-token-service", "test-identity-token-service");
    }

    @Test
    @DisplayName("Supports GCP GCE authentication")
    void testGcpGceAuthentication() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "gcp_gce")
                                .withEntry("gcp-path", "test-gcp-path")
                                .withEntry("gcp-service-account", "test-service-account")
                                .withEntry("role", "test-role")
                )
        );

    }

    @Test
    @DisplayName("Supports GCP IAM authentication")
    void testGcpIamAuthentication() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "gcp_iam")
                                .withEntry("credentials.json", "credentials JSON contents!")
                                .withEntry("encoded-key", "test-encoded-key")
                                .withEntry("gcp-path", "test-gcp-path")
                                .withEntry("gcp-project-id", "test-project-id")
                                .withEntry("gcp-service-account", "test-service-account")
                                .withEntry("jwt-validity", "test-jwt-validity")
                                .withEntry("role", "test-role")
                )
        );

        new VaultBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "gcp_iam")
                .containsEntry("spring.cloud.vault.gcp-iam.role", "test-role")
                .containsEntry("spring.cloud.vault.gcp-iam.credentials.location", "test-path/credentials.json")
                .containsEntry("spring.cloud.vault.gcp-iam.credentials.encoded-key", "test-encoded-key")
                .containsEntry("spring.cloud.vault.gcp-iam.gcp-path", "test-gcp-path")
                .containsEntry("spring.cloud.vault.gcp-iam.jwt-validity", "test-jwt-validity")
                .containsEntry("spring.cloud.vault.gcp-iam.project-id", "test-project-id")
                .containsEntry("spring.cloud.vault.gcp-iam.service-account-id", "test-service-account");
    }

    @Test
    @DisplayName("Supports Kubernetes authentication")
    void testK8sAuthentication() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "kubernetes")
                                .withEntry("role", "test-role")
                                .withEntry("kubernetes-path", "test-kubernetes-path")
                                .withEntry("kubernetes-service-account-token-file", "test-kubernetes-service-account-token-file")
                )
        );

        new VaultBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "kubernetes")
                .containsEntry("spring.cloud.vault.kubernetes.role", "test-role")
                .containsEntry("spring.cloud.vault.kubernetes.kubernetes-path", "test-kubernetes-path")
                .containsEntry("spring.cloud.vault.kubernetes.kubernetes-service-account-token-file", "test-kubernetes-service-account-token-file");
    }

    @Test
    @DisplayName("Doesn't fail when method is missing")
    void testMissingProvider() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                )
        );

        new VaultBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .doesNotContainKey("spring.cloud.vault.authentication");
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "token")
                                .withEntry("token", "test-token")
                ),
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("app-role-path", "test-app-role-path")
                                .withEntry("authentication-method", "approle")
                                .withEntry("role", "test-role")
                                .withEntry("role-id", "test-role-id")
                                .withEntry("secret-id", "test-secret-id")
                ),
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "cubbyhole")
                                .withEntry("token", "test-token")
                ),
                new Binding("test-name", Paths.get("test-path"),
                        baseSecret()
                                .withEntry("authentication-method", "cert")
                                .withEntry("cert-auth-path", "test-cert-auth-path")
                                .withEntry("key-store-password", "test-key-store-password")
                                .withEntry("keystore.jks", "key store contents!")
                )
        );

        environment.setProperty("org.springframework.cloud.bindings.boot.vault.enable", "false");

        new VaultBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }

    private FluentMap baseSecret() {
        return new FluentMap()
                .withEntry(Binding.TYPE, TYPE)
                .withEntry("namespace", "test-namespace")
                .withEntry("uri", "test-uri");
    }

}
