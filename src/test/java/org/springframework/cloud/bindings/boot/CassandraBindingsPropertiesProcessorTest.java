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
import static org.springframework.cloud.bindings.boot.CassandraBindingsPropertiesProcessor.KIND;

@DisplayName("Cassandra BindingsPropertiesProcessor")
final class CassandraBindingsPropertiesProcessorTest {

    private final Bindings bindings = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    Collections.singletonMap("kind", KIND),
                    new FluentMap()
                            .withEntry("cluster-name", "test-cluster-name")
                            .withEntry("compression", "test-compression")
                            .withEntry("contact-points", "test-contact-points")
                            .withEntry("keyspace-name", "test-keyspace-name")
                            .withEntry("password", "test-password")
                            .withEntry("port", "test-port")
                            .withEntry("ssl", "test-ssl")
                            .withEntry("username", "test-username")
            )
    );

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("contributes properties")
    void test() {
        new CassandraBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.data.cassandra.cluster-name", "test-cluster-name")
                .containsEntry("spring.data.cassandra.compression", "test-compression")
                .containsEntry("spring.data.cassandra.contact-points", "test-contact-points")
                .containsEntry("spring.data.cassandra.keyspace-name", "test-keyspace-name")
                .containsEntry("spring.data.cassandra.password", "test-password")
                .containsEntry("spring.data.cassandra.port", "test-port")
                .containsEntry("spring.data.cassandra.ssl", "test-ssl")
                .containsEntry("spring.data.cassandra.username", "test-username");
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.cassandra.enable", "false");

        new CassandraBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }

}
