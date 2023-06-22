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
import static org.springframework.cloud.bindings.boot.ArtemisBindingsPropertiesProcessor.TYPE;

@DisplayName("ActiveMQ Artemis BindingsPropertiesProcessor")
final class ArtemisBindingsPropertiesProcessorTest {

    private final Bindings bindingsSpringBoot3 = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry(Binding.TYPE, TYPE)
                            .withEntry("mode", "EMBEDDED")
                            .withEntry("broker-url", "tcp://test-host:test-port")
                            .withEntry("user", "test-user")
                            .withEntry("password", "test-password")
            )
    );

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("contributes properties")
    void testSb3() {
        new ArtemisBindingsPropertiesProcessor().process(environment, bindingsSpringBoot3, properties);
        assertThat(properties)
                .containsEntry("spring.artemis.mode", "EMBEDDED")
                .containsEntry("spring.artemis.broker-url", "tcp://test-host:test-port")
                .containsEntry("spring.artemis.password", "test-password")
                .containsEntry("spring.artemis.user", "test-user");
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.artemis.enable", "false");

        new ArtemisBindingsPropertiesProcessor().process(environment, bindingsSpringBoot3, properties);
        assertThat(properties).isEmpty();
    }

}
