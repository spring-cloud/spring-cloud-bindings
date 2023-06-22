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
import static org.springframework.cloud.bindings.boot.ElasticsearchBindingsPropertiesProcessor.TYPE;

@DisplayName("Elasticsearch BindingsPropertiesProcessor")
final class ElasticsearchBindingsPropertiesProcessorTest {

    private final Bindings bindingsSpringBoot3 = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry(Binding.TYPE, TYPE)
                            .withEntry("password", "test-password")
                            .withEntry("uris", "test-uris")
                            .withEntry("username", "test-username")
            )
    );


    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("contributes properties - Spring Boot 2 flavor")
    void testSb3() {
        new ElasticsearchBindingsPropertiesProcessor().process(environment, bindingsSpringBoot3, properties);
        assertThat(properties)
                .containsEntry("spring.elasticsearch.password", "test-password")
                .containsEntry("spring.elasticsearch.uris", "test-uris")
                .containsEntry("spring.elasticsearch.username", "test-username");
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.elasticsearch.enable", "false");

        new ElasticsearchBindingsPropertiesProcessor().process(environment, bindingsSpringBoot3, properties);
        assertThat(properties).isEmpty();
    }

}
