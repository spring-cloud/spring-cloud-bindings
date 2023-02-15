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

    private final FluentMap secretSsl = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("database", "test-database")
            .withEntry("host", "test-host")
            .withEntry("password", "test-password")
            .withEntry("port", "test-port")
            .withEntry("username", "test-username")
            .withEntry("sslmode", "verify-full")
            .withEntry("sslrootcert", "root.cert")
            .withEntry("options", "--cluster=routing-id&opt=val1");

    @Test
    @DisplayName("composes jdbc url from host port and database with sslmode and crdb option")
    void testJdbcWithSsl() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("bindings"), secretSsl)
        );
        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.password", "test-password")
                .containsEntry("spring.datasource.url", "jdbc:postgresql://test-host:test-port/test-database?sslmode=verify-full&sslrootcert=bindings/root.cert&options=--cluster=routing-id -c opt=val1")
                .containsEntry("spring.datasource.username", "test-username");
    }

    private final FluentMap secretInvalidCrdbOption = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("database", "test-database")
            .withEntry("host", "test-host")
            .withEntry("password", "test-password")
            .withEntry("port", "test-port")
            .withEntry("username", "test-username")
            .withEntry("sslmode", "verify-full")
            .withEntry("sslrootcert", "root.cert")
            .withEntry("options", "-cluster=routing-id&opt=val1");

    @Test
    @DisplayName("composes jdbc url from host port and database with sslmode and crdb option")
    void testJdbcWithInvalidCrdbOption() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("bindings"), secretInvalidCrdbOption)
        );
        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.password", "test-password")
                .containsEntry("spring.datasource.url", "jdbc:postgresql://test-host:test-port/test-database?sslmode=verify-full&sslrootcert=bindings/root.cert&options=-c -cluster=routing-id -c opt=val1")
                .containsEntry("spring.datasource.username", "test-username");
    }

    @Test
    @DisplayName("composes r2dbc url from host port and database with sslmode and crdb option")
    void testR2dbcWithSsl() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("bindings"), secretSsl)
        );
        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.r2dbc.password", "test-password")
                .containsEntry("spring.r2dbc.url", "r2dbc:postgresql://test-host:test-port/test-database?sslmode=verify-full&sslrootcert=bindings/root.cert&options=--cluster=routing-id -c opt=val1")
                .containsEntry("spring.r2dbc.username", "test-username");
    }

    private final FluentMap secretSslDisable = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("database", "test-database")
            .withEntry("host", "test-host")
            .withEntry("password", "test-password")
            .withEntry("port", "test-port")
            .withEntry("username", "test-username")
            .withEntry("sslmode", "disable");

    @Test
    @DisplayName("composes jdbc url from host port and database with sslmode disable")
    void testJdbcWithSslDisable() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("bindings"), secretSslDisable)
        );
        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.password", "test-password")
                .containsEntry("spring.datasource.url", "jdbc:postgresql://test-host:test-port/test-database?sslmode=disable")
                .containsEntry("spring.datasource.username", "test-username");
    }

    private final FluentMap secretWithDBoptions = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("database", "test-database")
            .withEntry("host", "test-host")
            .withEntry("password", "test-password")
            .withEntry("port", "test-port")
            .withEntry("username", "test-username")
            .withEntry("options", "opt1=val1&opt2=val2");

    @Test
    @DisplayName("composes jdbc url from host port and database with DB options")
    void testJdbcWithDBoptions() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("bindings"), secretWithDBoptions)
        );
        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.password", "test-password")
                .containsEntry("spring.datasource.url", "jdbc:postgresql://test-host:test-port/test-database?options=-c opt1=val1 -c opt2=val2")
                .containsEntry("spring.datasource.username", "test-username");
    }

    private final FluentMap secretWithInvalidDBOptions = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("database", "test-database")
            .withEntry("host", "test-host")
            .withEntry("password", "test-password")
            .withEntry("port", "test-port")
            .withEntry("username", "test-username")
            .withEntry("options", "opt1=val1&opt");

    @Test
    @DisplayName("composes jdbc url from host port and database with invalid DB options")
    void testJdbcWithInvaildDBoptions() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("bindings"), secretWithInvalidDBOptions)
        );
        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.password", "test-password")
                .containsEntry("spring.datasource.url", "jdbc:postgresql://test-host:test-port/test-database?options=-c opt1=val1")
                .containsEntry("spring.datasource.username", "test-username");
    }

    private final FluentMap secretWithEmptyDBOptions = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("database", "test-database")
            .withEntry("host", "test-host")
            .withEntry("password", "test-password")
            .withEntry("port", "test-port")
            .withEntry("username", "test-username")
            .withEntry("options", "");

    @Test
    @DisplayName("composes jdbc url from host port and database with empty DB options")
    void testJdbcWithEmptyDBoptions() {
        Bindings bindings = new Bindings(
                new Binding("test-name", Paths.get("bindings"), secretWithEmptyDBOptions)
        );
        new PostgreSqlBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.password", "test-password")
                .containsEntry("spring.datasource.url", "jdbc:postgresql://test-host:test-port/test-database")
                .containsEntry("spring.datasource.username", "test-username");
    }
}
