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
import static org.springframework.cloud.bindings.boot.EurekaBindingsPropertiesProcessor.TYPE;

@DisplayName("Eureka BindingsPropertiesProcessor")
final class EurekaBindingsPropertiesProcessorTest {

    private final Bindings bindings = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry(Binding.TYPE, TYPE)
                            .withEntry("uri", "test-uri")
                            .withEntry("client-id", "test-client-id")
                            .withEntry("client-secret", "test-client-secret")
                            .withEntry("access-token-uri", "test-access-token-uri")
            )
    );

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("contributes properties")
    void test() {
        new EurekaBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("eureka.client.region", "default")
                .containsEntry("eureka.client.oauth2.client-id", "test-client-id")
                .containsEntry("eureka.client.oauth2.access-token-uri", "test-access-token-uri")
                .containsEntry("eureka.client.serviceUrl.defaultZone", "test-uri/eureka/");
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.eureka.enable", "false");

        new KafkaBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }

}
