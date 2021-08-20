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
import static org.springframework.cloud.bindings.boot.PostgreSqlBindingsPropertiesProcessor.TYPE;

@DisplayName("PostgreSQL BindingsPropertiesProcessor")
final class PostgreSqlBindingsPropertiesProcessorTest {

    private final FluentMap secret = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("database", "test-database")
            .withEntry("host", "test-host")
            .withEntry("password", "test-password")
            .withEntry("port", "test-port")
            .withEntry("username", "test-username");

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("composes jdbc url from host port and database")
    void testJdbc() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"), secret)
        );
        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.password", "test-password")
                .containsEntry("spring.datasource.url", "jdbc:postgresql://test-host:test-port/test-database")
                .containsEntry("spring.datasource.username", "test-username");
    }

    @Test
    @DisplayName("gives precedence to jdbc-url")
    void testJdbcURL() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"), secret.withEntry("jdbc-url", "test-jdbc-url"))
        );
        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.password", "test-password")
                .containsEntry("spring.datasource.url", "test-jdbc-url")
                .containsEntry("spring.datasource.username", "test-username");
    }

    @Test
    @DisplayName("composes r2dbc url from host port and database")
    void testR2dbc() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"), secret)
        );
        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.r2dbc.password", "test-password")
                .containsEntry("spring.r2dbc.url", "r2dbc:postgresql://test-host:test-port/test-database")
                .containsEntry("spring.r2dbc.username", "test-username");
    }

    @Test
    @DisplayName("gives precedence to r2dbc-url")
    void testR2dbcURL() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"), secret.withEntry("r2dbc-url", "test-r2dbc-url"))
        );
        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.r2dbc.password", "test-password")
                .containsEntry("spring.r2dbc.url", "test-r2dbc-url")
                .containsEntry("spring.r2dbc.username", "test-username");
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("test-path"), secret)
        );
        environment.setProperty("org.springframework.cloud.bindings.boot.postgresql.enable", "false");

        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }

}
