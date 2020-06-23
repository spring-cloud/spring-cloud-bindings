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

    private final Binding awsEc2Binding = new Binding(
            "test-name", Paths.get("test-path"),
            metadata(),
            baseSecret()
                    .withEntry("method", "aws_ec2")
                    .withEntry("role", "test-role")
                    .withEntry("aws-ec2-path", "test-aws-ec2-path")
                    .withEntry("identity-document", "test-identity-document")
                    .withEntry("nonce", "test-nonce")
    );

    private final Binding awsIamBinding = new Binding(
            "test-name", Paths.get("test-path"),
            metadata(),
            baseSecret()
                    .withEntry("method", "aws_iam")
                    .withEntry("role", "test-role")
                    .withEntry("aws-path", "test-aws-path")
                    .withEntry("server-id", "test-server-id")
                    .withEntry("endpoint-uri", "test-endpoint-uri")
    );

    private final Binding azureMsiBinding = new Binding(
            "test-name", Paths.get("test-path"),
            metadata(),
            baseSecret()
                    .withEntry("method", "azure_msi")
                    .withEntry("role", "test-role")
                    .withEntry("azure-path", "test-azure-path")
    );

    private final Binding gcpGceBinding = new Binding(
            "test-name", Paths.get("test-path"),
            metadata(),
            baseSecret()
                    .withEntry("method", "gcp_gce")
                    .withEntry("role", "test-role")
                    .withEntry("gcp-path", "test-gcp-path")
                    .withEntry("service-account", "test-service-account")
    );

    private final Binding gcpIamBinding = new Binding(
            "test-name", Paths.get("test-path"),
            metadata(),
            baseSecret()
                    .withEntry("method", "gcp_iam")
                    .withEntry("credentials.json", "credentials JSON contents!")
                    .withEntry("encoded-key", "test-encoded-key")
                    .withEntry("gcp-path", "test-gcp-path")
                    .withEntry("jwt-validity", "test-jwt-validity")
                    .withEntry("project-id", "test-project-id")
                    .withEntry("role", "test-role")
                    .withEntry("service-account-id", "test-service-account-id")
    );

    private final Binding k8sBinding = new Binding(
            "test-name", Paths.get("test-path"),
            metadata(),
            baseSecret()
                    .withEntry("method", "kubernetes")
                    .withEntry("role", "test-role")
                    .withEntry("kubernetes-path", "test-kubernetes-path")
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
                .containsEntry("spring.cloud.vault.authentication", "token")
                .containsEntry("spring.cloud.vault.token", "test-token");
    }

    @Test
    @DisplayName("Supports AppRole authentication")
    void testAppRoleAuthentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(appRoleBinding), properties);
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
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(cubbyholeBinding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "cubbyhole")
                .containsEntry("spring.cloud.vault.token", "test-token");
    }

    @Test
    @DisplayName("Supports TLS certificate authentication")
    void testCertAuthentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(certBinding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "cert")
                .containsEntry("spring.cloud.vault.ssl.key-store", "test-path/secret/keystore.jks")
                .containsEntry("spring.cloud.vault.ssl.key-store-password", "test-key-store-password")
                .containsEntry("spring.cloud.vault.ssl.cert-auth-path", "test-cert-auth-path");
    }

    @Test
    @DisplayName("Supports AWS EC2 authentication")
    void testAwsEc2Authentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(awsEc2Binding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "aws_ec2")
                .containsEntry("spring.cloud.vault.aws-ec2.role", "test-role")
                .containsEntry("spring.cloud.vault.aws-ec2.aws-ec2-path", "test-aws-ec2-path")
                .containsEntry("spring.cloud.vault.aws-ec2.identity-document", "test-identity-document")
                .containsEntry("spring.cloud.vault.aws-ec2.nonce", "test-nonce");
    }

    @Test
    @DisplayName("Supports AWS IAM authentication")
    void testAwsIamAuthentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(awsIamBinding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "aws_iam")
                .containsEntry("spring.cloud.vault.aws-iam.role", "test-role")
                .containsEntry("spring.cloud.vault.aws-iam.aws-path", "test-aws-path")
                .containsEntry("spring.cloud.vault.aws-iam.server-id", "test-server-id")
                .containsEntry("spring.cloud.vault.aws-iam.endpoint-uri", "test-endpoint-uri");
    }

    @Test
    @DisplayName("Supports Azure MSI authentication")
    void testAzureMsiAuthentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(azureMsiBinding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "azure_msi")
                .containsEntry("spring.cloud.vault.azure-msi.role", "test-role")
                .containsEntry("spring.cloud.vault.azure-msi.azure-path", "test-azure-path");
    }

    @Test
    @DisplayName("Supports GCP GCE authentication")
    void testGcpGceAuthentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(gcpGceBinding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "gcp_gce")
                .containsEntry("spring.cloud.vault.gcp-gce.role", "test-role")
                .containsEntry("spring.cloud.vault.gcp-gce.gcp-path", "test-gcp-path")
                .containsEntry("spring.cloud.vault.gcp-gce.service-account", "test-service-account");
    }

    @Test
    @DisplayName("Supports GCP IAM authentication")
    void testGcpIamAuthentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(gcpIamBinding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "gcp_iam")
                .containsEntry("spring.cloud.vault.gcp-iam.role", "test-role")
                .containsEntry("spring.cloud.vault.gcp-iam.credentials.location", "test-path/secret/credentials.json")
                .containsEntry("spring.cloud.vault.gcp-iam.credentials.encoded-key", "test-encoded-key")
                .containsEntry("spring.cloud.vault.gcp-iam.gcp-path", "test-gcp-path")
                .containsEntry("spring.cloud.vault.gcp-iam.jwt-validity", "test-jwt-validity")
                .containsEntry("spring.cloud.vault.gcp-iam.project-id", "test-project-id")
                .containsEntry("spring.cloud.vault.gcp-iam.service-account-id", "test-service-account-id");
    }

    @Test
    @DisplayName("Supports Kubernetes authentication")
    void testK8sAuthentication() {
        new VaultBindingsPropertiesProcessor().process(environment, new Bindings(k8sBinding), properties);
        assertThat(properties)
                .containsEntry("spring.cloud.vault.uri", "test-uri")
                .containsEntry("spring.cloud.vault.namespace", "test-namespace")
                .containsEntry("spring.cloud.vault.authentication", "kubernetes")
                .containsEntry("spring.cloud.vault.kubernetes.role", "test-role")
                .containsEntry("spring.cloud.vault.kubernetes.kubernetes-path", "test-kubernetes-path");
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
