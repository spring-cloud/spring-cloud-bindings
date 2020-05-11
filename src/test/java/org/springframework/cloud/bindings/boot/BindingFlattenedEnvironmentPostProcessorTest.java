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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.mock.env.MockEnvironment;

import java.nio.file.Paths;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Binding-flattened EnvironmentPostProcessor")
final class BindingFlattenedEnvironmentPostProcessorTest {

    private final SpringApplication application = new SpringApplication();

    private final MockEnvironment environment = new MockEnvironment();

    @Test
    @DisplayName("is disabled by default")
    @ClearSystemProperty(key = "org.springframework.cloud.bindings.boot.enable")
    void disabledByDefault() {
        new BindingFlattenedEnvironmentPostProcessor(
                new Bindings(
                        new Binding("test-name", Paths.get("test-path"),
                                Collections.emptyMap(), Collections.emptyMap())
                )
        ).postProcessEnvironment(environment, application);

        assertThat(environment.getPropertySources()).hasSize(1);
    }

    @Nested
    @DisplayName("when enabled")
    @SetSystemProperty(key = "org.springframework.cloud.bindings.boot.enable", value = "true")
    final class Enabled {

        @Test
        @DisplayName("does not create PropertySource if no bindings")
        void noBindings() {
            new BindingFlattenedEnvironmentPostProcessor(new Bindings()).postProcessEnvironment(environment, application);

            assertThat(environment.getPropertySources()).hasSize(1);
        }

        @Test
        @DisplayName("creates PropertySource with properties")
        void containsProperties() {
            new BindingFlattenedEnvironmentPostProcessor(
                    new Bindings(
                            new Binding("test-name", Paths.get("test-path"),
                                    Collections.singletonMap("test-metadata-key", "test-metadata-value"),
                                    Collections.singletonMap("test-secret-key", "test-secret-value"))
                    )
            ).postProcessEnvironment(environment, application);

            assertThat(environment.getPropertySources()).hasSize(2);
            assertThat(environment.getProperty("cnb.bindings.test-name.metadata.test-metadata-key")).isEqualTo("test-metadata-value");
            assertThat(environment.getProperty("cnb.bindings.test-name.secret.test-secret-key")).isEqualTo("test-secret-value");
        }

        @Test
        @DisplayName("has order before ConfigFileApplicationListener")
        void order() {
            assertThat(new BindingFlattenedEnvironmentPostProcessor(new Bindings()).getOrder())
                    .isLessThan(ConfigFileApplicationListener.DEFAULT_ORDER);
        }

    }

}
