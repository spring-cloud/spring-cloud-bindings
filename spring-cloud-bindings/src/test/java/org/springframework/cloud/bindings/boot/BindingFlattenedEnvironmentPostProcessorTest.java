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

import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.cloud.bindings.FluentMap;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@DisplayName("Binding-flattened EnvironmentPostProcessor")
@ExtendWith(OutputCaptureExtension.class)
final class BindingFlattenedEnvironmentPostProcessorTest {

    private final SpringApplication application = new SpringApplication();

    private final MockEnvironment environment = new MockEnvironment()
            .withProperty("org.springframework.cloud.bindings.boot.enable", "true");

    @Test
    @DisplayName("is disabled by default")
    void disabledByDefault() {
        new BindingFlattenedEnvironmentPostProcessor(
                new Bindings(
                        new Binding("test-name", Paths.get("test-path"),
                                new FluentMap()
                                        .withEntry(Binding.TYPE, "test-type")
                        )
                )
        ).postProcessEnvironment(new MockEnvironment(), application);

        assertThat(environment.getPropertySources()).hasSize(1);
    }


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
                                new FluentMap()
                                        .withEntry(Binding.TYPE, "test-type")
                                        .withEntry("test-secret-key", "test-secret-value")
                        )
                )
        ).postProcessEnvironment(environment, application);

        assertThat(environment.getPropertySources()).hasSize(2);
        assertThat(environment.getProperty("k8s.bindings.test-name.test-secret-key")).isEqualTo("test-secret-value");
    }

    @Test
    @DisplayName("has order before the EnvironmentPostProcessor listener")
    void order() {
        assertThat(new BindingFlattenedEnvironmentPostProcessor(new Bindings()).getOrder())
                .isLessThan(Ordered.HIGHEST_PRECEDENCE + 10);
    }

    @Test
    @DisplayName("creates PropertySource when the environment is prepared")
    void environmentPreparedEvent() {
        BindingFlattenedEnvironmentPostProcessor listener = new BindingFlattenedEnvironmentPostProcessor(
                new Bindings(
                        new Binding("test-name", Paths.get("test-path"),
                                new FluentMap()
                                        .withEntry(Binding.TYPE, "test-type")
                                        .withEntry("test-secret-key", "test-secret-value")
                        )
                )
        );

        ApplicationEnvironmentPreparedEvent event = new ApplicationEnvironmentPreparedEvent(
                new DefaultBootstrapContext(), application, new String[0], environment);

        assertThat(listener.supportsEventType(ApplicationEnvironmentPreparedEvent.class)).isTrue();
        listener.onApplicationEvent(event);

        assertThat(environment.getProperty("k8s.bindings.test-name.test-secret-key")).isEqualTo("test-secret-value");
    }

    @Test
    @DisplayName("ignores unsupported events")
    void unsupportedEvent() {
        BindingFlattenedEnvironmentPostProcessor listener = new BindingFlattenedEnvironmentPostProcessor(new Bindings());

        assertThat(listener.supportsEventType(org.springframework.context.event.ContextRefreshedEvent.class)).isFalse();
        assertThat(listener.supportsEventType(ApplicationPreparedEvent.class)).isTrue();
    }

    @Test
    @DisplayName("replays the deferred log when the application is prepared")
    void applicationPreparedEvent(CapturedOutput output) {
        BindingFlattenedEnvironmentPostProcessor listener = new BindingFlattenedEnvironmentPostProcessor(
                new Bindings(
                        new Binding("test-name", Paths.get("test-path"),
                                new FluentMap()
                                        .withEntry(Binding.TYPE, "test-type")
                                        .withEntry("test-secret-key", "test-secret-value")
                        )
                )
        );
        listener.postProcessEnvironment(environment, application);
        assertThat(output).doesNotContain("Creating flattened PropertySource");

        listener.onApplicationEvent(new ApplicationPreparedEvent(
                application, new String[0], mock(ConfigurableApplicationContext.class)));

        assertThat(output).contains("Creating flattened PropertySource from Kubernetes Service Bindings");
    }

}
