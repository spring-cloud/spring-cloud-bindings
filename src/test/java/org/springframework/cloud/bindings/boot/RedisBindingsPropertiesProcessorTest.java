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
import static org.springframework.cloud.bindings.boot.RedisBindingsPropertiesProcessor.TYPE;

@DisplayName("Redis BindingsPropertiesProcessor")
final class RedisBindingsPropertiesProcessorTest {

    private final Bindings bindings = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry(Binding.TYPE, TYPE)
                            .withEntry("client-name", "test-client-name")
                            .withEntry("cluster.max-redirects", "test-cluster-max-redirects")
                            .withEntry("cluster.nodes", "test-cluster-nodes")
                            .withEntry("database", "test-database")
                            .withEntry("host", "test-host")
                            .withEntry("password", "test-password")
                            .withEntry("port", "test-port")
                            .withEntry("sentinel.master", "test-sentinel-master")
                            .withEntry("sentinel.nodes", "test-sentinel-nodes")
                            .withEntry("ssl", "test-ssl")
                            .withEntry("url", "test-url")
            )
    );

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("contributes properties")
    void test() {
        new RedisBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.data.redis.client-name", "test-client-name")
                .containsEntry("spring.data.redis.cluster.max-redirects", "test-cluster-max-redirects")
                .containsEntry("spring.data.redis.cluster.nodes", "test-cluster-nodes")
                .containsEntry("spring.data.redis.database", "test-database")
                .containsEntry("spring.data.redis.host", "test-host")
                .containsEntry("spring.data.redis.password", "test-password")
                .containsEntry("spring.data.redis.port", "test-port")
                .containsEntry("spring.data.redis.sentinel.master", "test-sentinel-master")
                .containsEntry("spring.data.redis.sentinel.nodes", "test-sentinel-nodes")
                .containsEntry("spring.data.redis.ssl", "test-ssl")
                .containsEntry("spring.data.redis.url", "test-url");
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.redis.enable", "false");

        new RedisBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }

}
