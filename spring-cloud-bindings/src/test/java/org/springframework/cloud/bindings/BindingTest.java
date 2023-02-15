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
package org.springframework.cloud.bindings;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("Binding")
final class BindingTest {

    @Test
    @DisplayName("fails to create invalid binding")
    void testInvalid() throws IOException {
        Path path = Files.createTempDirectory("invalid-binding");

        assertThatIllegalArgumentException().isThrownBy(() -> new Binding(path));
    }

    @Nested
    @DisplayName("CNB Bindings")
    final class CNBBindings {

        private final Path root = Paths.get("src/test/resources/cnb");

        @Test
        @DisplayName("populates content from filesystem")
        void test() {
            Binding binding = new Binding(root.resolve("test-name-1"));

            assertThat(binding.getType()).isEqualTo("test-kind-1");
            assertThat(binding.getProvider()).isEqualTo("test-provider-1");
            assertThat(binding.getSecretFilePath("test-metadata-key"))
                    .isEqualTo(root.resolve("test-name-1/metadata/test-metadata-key"));
            assertThat(binding.getSecretFilePath("test-secret-key"))
                    .isEqualTo(root.resolve("test-name-1/secret/test-secret-key"));
        }

        @Test
        @DisplayName("populates k8s style content from filesystem")
        void testK8s() {
            //When bindings are provided as a k8s configmap secret pairs data files will be symlinks to hidden directories
            Binding binding = new Binding(root.resolve("test-k8s"));

            assertThat(binding.getType()).isEqualTo("test-kind-1");
            assertThat(binding.getProvider()).isEqualTo("test-provider-1");
            assertThat(binding.getSecretFilePath("test-metadata-key"))
                    .isEqualTo(root.resolve("test-k8s/metadata/test-metadata-key"));
            assertThat(binding.getSecretFilePath("test-secret-key"))
                    .isEqualTo(root.resolve("test-k8s/secret/test-secret-key"));
        }
    }

    @Nested
    @DisplayName("Kubernetes Bindings")
    final class KubernetesBindings {

        private final Path root = Paths.get("src/test/resources/k8s");

        @Test
        @DisplayName("populates content from filesystem")
        void test() {
            Binding binding = new Binding(root.resolve("test-name-1"));

            assertThat(binding.getType()).isEqualTo("test-type-1");
            assertThat(binding.getProvider()).isEqualTo("test-provider-1");
            assertThat(binding.getSecretFilePath("test-secret-key"))
                    .isEqualTo(root.resolve("test-name-1/test-secret-key"));
        }

        @Test
        @DisplayName("populates k8s style content from filesystem")
        void testK8s() {
            //When bindings are provided as a k8s configmap secret pairs data files will be symlinks to hidden directories
            Binding binding = new Binding(root.resolve("test-k8s"));

            assertThat(binding.getType()).isEqualTo("test-type-1");
            assertThat(binding.getProvider()).isEqualTo("test-provider-1");
            assertThat(binding.getSecretFilePath("test-secret-key"))
                    .isEqualTo(root.resolve("test-k8s/test-secret-key"));
        }

    }

}
