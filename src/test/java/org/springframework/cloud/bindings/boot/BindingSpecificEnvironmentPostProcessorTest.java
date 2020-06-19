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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.mock.env.MockEnvironment;

import java.nio.file.Paths;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Binding-specific EnvironmentPostProcessor")
final class BindingSpecificEnvironmentPostProcessorTest {

    private final SpringApplication application = new SpringApplication();

    private final MockEnvironment environment = new MockEnvironment()
            .withProperty("org.springframework.cloud.bindings.boot.enable", "true");

    @Test
    @DisplayName("is disabled by default")
    void disabledByDefault() {
        new BindingSpecificEnvironmentPostProcessor(
                new Bindings(
                        new Binding("test-name", Paths.get("test-path"),
                                Collections.emptyMap(), Collections.emptyMap())
                ),
                (environment, bindings, properties) -> properties.put("test-key", "test-value")
        ).postProcessEnvironment(new MockEnvironment(), application);

        assertThat(environment.getPropertySources()).hasSize(1);
    }


    @Test
    @DisplayName("does not create PropertySource if no bindings")
    void noBindings() {
        new BindingSpecificEnvironmentPostProcessor(new Bindings()).postProcessEnvironment(environment, application);

        assertThat(environment.getPropertySources()).hasSize(1);
    }

    @Test
    @DisplayName("does not create PropertySource if no properties")
    void noProperties() {
        new BindingSpecificEnvironmentPostProcessor(
                new Bindings(
                        new Binding("test-name", Paths.get("test-path"),
                                Collections.emptyMap(), Collections.emptyMap())
                )
        ).postProcessEnvironment(environment, application);

        assertThat(environment.getPropertySources()).hasSize(1);
    }

    @Test
    @DisplayName("creates PropertySource with properties")
    void containsProperties() {
        new BindingSpecificEnvironmentPostProcessor(
                new Bindings(
                        new Binding("test-name", Paths.get("test-path"),
                                Collections.emptyMap(), Collections.emptyMap())
                ),
                (environment, bindings, properties) -> properties.put("test-key", "test-value")
        ).postProcessEnvironment(environment, application);

        assertThat(environment.getPropertySources()).hasSize(2);
        assertThat(environment.getProperty("test-key")).isEqualTo("test-value");
    }

    @Test
    @DisplayName("has order before ConfigFileApplicationListener")
    void order() {
        assertThat(new BindingSpecificEnvironmentPostProcessor(new Bindings()).getOrder())
                .isLessThan(ConfigFileApplicationListener.DEFAULT_ORDER);
    }

    @Test
    @DisplayName("included implementations are registered")
    void includedImplementations() {
        assertThat(new BindingSpecificEnvironmentPostProcessor().processors).hasSize(18);
    }

}
