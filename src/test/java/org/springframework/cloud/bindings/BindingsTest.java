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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@DisplayName("Bindings")
final class BindingsTest {

    @Nested
    @DisplayName("CNB Bindings")
    final class CNBBindings {

        private final Path root = Paths.get("src/test/resources/cnb");

        @Nested
        @DisplayName("when constructed")
        final class Constructor {

            @Test
            @DisplayName("empty if path is null")
            void nullPath() {
                Bindings b = new Bindings((String) null);

                assertThat(b.getBindings()).isEmpty();
            }

            @Test
            @DisplayName("empty if path does not exist")
            void nonExistentDirectory() {
                Bindings b = new Bindings(root.resolve("non-existent").toString());

                assertThat(b.getBindings()).isEmpty();
            }

            @Test
            @DisplayName("throws exception if path is not a directory")
            void nonDirectory() throws IOException {
                Path path = File.createTempFile("bindings", "").toPath();

                assertThatIllegalArgumentException().isThrownBy(() -> new Bindings(path.toString()));
            }

            @Test
            @DisplayName("populates content")
            void construct() {
                Bindings b = new Bindings(root.toString());

                assertThat(b.getBindings()).hasSize(3);
            }

        }

        @Nested
        @DisplayName("with content")
        final class Content {

            private final Bindings bindings = new Bindings(
                    new Binding("test-name-1", root.resolve("test-name-1"),
                            new FluentMap()
                                    .withEntry("kind", "test-kind-1")
                                    .withEntry("provider", "test-provider-1")
                    ),
                    new Binding("test-name-2", root.resolve("test-name-2"),
                            new FluentMap()
                                    .withEntry("kind", "test-kind-2")
                                    .withEntry("provider", "test-provider-2")
                    )
            );

            @Test
            @DisplayName("returns content")
            void getBindings() {
                assertThat(bindings.getBindings()).hasSize(2);
            }

            @Test
            @DisplayName("filters bindings by kind")
            void filterBindingsByKind() {
                assertThat(bindings.filterBindings("test-kind-1", null)).hasSize(1);
            }

            @Test
            @DisplayName("filters bindings by provider")
            void filterBindingsByProvider() {
                assertThat(bindings.filterBindings(null, "test-provider-1")).hasSize(1);
            }

        }

    }

    @Nested
    @DisplayName("Kubernetes Bindings")
    final class KubernetesBindings {

        private final Path root = Paths.get("src/test/resources/k8s");

        @Nested
        @DisplayName("when constructed")
        final class Constructor {

            @Test
            @DisplayName("empty if path is null")
            void nullPath() {
                Bindings b = new Bindings((String) null);

                assertThat(b.getBindings()).isEmpty();
            }

            @Test
            @DisplayName("empty if path does not exist")
            void nonExistentDirectory() {
                Bindings b = new Bindings(root.resolve("non-existent").toString());

                assertThat(b.getBindings()).isEmpty();
            }

            @Test
            @DisplayName("throws exception if path is not a directory")
            void nonDirectory() throws IOException {
                Path path = File.createTempFile("bindings", "").toPath();

                assertThatIllegalArgumentException().isThrownBy(() -> new Bindings(path.toString()));
            }

            @Test
            @DisplayName("populates content")
            void construct() {
                Bindings b = new Bindings(root.toString());

                assertThat(b.getBindings()).hasSize(3);
            }

        }

        @Nested
        @DisplayName("with content")
        final class Content {

            private final Bindings bindings = new Bindings(
                    new Binding("test-name-1", root.resolve("test-name-1"),
                            new FluentMap()
                                    .withEntry("type", "test-type-1")
                                    .withEntry("provider", "test-provider-1")
                    ),
                    new Binding("test-name-2", root.resolve("test-name-2"),
                            new FluentMap()
                                    .withEntry("type", "test-type-2")
                                    .withEntry("provider", "test-provider-2")
                    )
            );

            @Test
            @DisplayName("returns content")
            void getBindings() {
                assertThat(bindings.getBindings()).hasSize(2);
            }

            @Test
            @DisplayName("filters bindings by kind")
            void filterBindingsByKind() {
                assertThat(bindings.filterBindings("test-type-1", null)).hasSize(1);
            }

            @Test
            @DisplayName("filters bindings by provider")
            void filterBindingsByProvider() {
                assertThat(bindings.filterBindings(null, "test-provider-1")).hasSize(1);
            }

        }

    }

}
