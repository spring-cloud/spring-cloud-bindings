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
import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.ElasticsearchBindingsPropertiesProcessor.KIND;

@DisplayName("Elasticsearch BindingsPropertiesProcessor")
final class ElasticsearchBindingsPropertiesProcessorTest {

    private final Bindings bindings = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    Collections.singletonMap("kind", KIND),
                    new FluentMap()
                            .withEntry("endpoints", "test-endpoints")
                            .withEntry("password", "test-password")
                            .withEntry("use-ssl", "test-use-ssl")
                            .withEntry("username", "test-username")
                            .withEntry("proxy.host", "test-proxy-host")
                            .withEntry("proxy.port", "test-proxy-port")
                            .withEntry("uris", "test-uris")
            )
    );

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("contributes properties")
    void test() {
        new ElasticsearchBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.data.elasticsearch.client.reactive.endpoints", "test-endpoints")
                .containsEntry("spring.data.elasticsearch.client.reactive.password", "test-password")
                .containsEntry("spring.data.elasticsearch.client.reactive.use-ssl", "test-use-ssl")
                .containsEntry("spring.data.elasticsearch.client.reactive.username", "test-username")
                .containsEntry("spring.elasticsearch.jest.password", "test-password")
                .containsEntry("spring.elasticsearch.jest.proxy.host", "test-proxy-host")
                .containsEntry("spring.elasticsearch.jest.proxy.port", "test-proxy-port")
                .containsEntry("spring.elasticsearch.jest.username", "test-username")
                .containsEntry("spring.elasticsearch.rest.password", "test-password")
                .containsEntry("spring.elasticsearch.rest.uris", "test-uris")
                .containsEntry("spring.elasticsearch.rest.username", "test-username");
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.elasticsearch.enable", "false");

        new ElasticsearchBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }

}
