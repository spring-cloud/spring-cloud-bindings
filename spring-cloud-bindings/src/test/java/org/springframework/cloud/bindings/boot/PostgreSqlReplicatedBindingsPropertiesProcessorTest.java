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

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.PostgreSqlReplicatedBindingsPropertiesProcessor.TYPE;

import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.FluentMap;
import org.springframework.mock.env.MockEnvironment;

@DisplayName("PostgreSQLReplicated BindingsPropertiesProcessor")
public class PostgreSqlReplicatedBindingsPropertiesProcessorTest {

    private final FluentMap secret = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("rw-database", "testrw-database")
            .withEntry("rw-host", "testrw-host")
            .withEntry("rw-password", "testrw-password")
            .withEntry("rw-port", "testrw-port")
            .withEntry("rw-username", "testrw-username")
            .withEntry("ro-database", "testro-database")
            .withEntry("ro-host", "testro-host")
            .withEntry("ro-password", "testro-password")
            .withEntry("ro-port", "testro-port")
            .withEntry("ro-username", "testro-username");
    
    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();
    
    @Test
    @DisplayName("composes jdbc url from host port and database")
    void testJdbc() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated.rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated.rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated.rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database")
                .containsEntry("spring.datasource.replicated.rw.username", "testrw-username")
                .containsEntry("spring.datasource.replicated.ro.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated.ro.password", "testro-password")
                .containsEntry("spring.datasource.replicated.ro.url", "jdbc:postgresql://testro-host:testro-port/testro-database")
                .containsEntry("spring.datasource.replicated.ro.username", "testro-username")                
                .containsEntry("spring.datasource.replicated.name", "dbrw");
        
    }  
    

    @Test
    @DisplayName("gives precedence to jdbc-url")
    void testJdbcURL() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret.withEntry("rw-jdbc-url", "testrw-jdbc-url").withEntry("ro-jdbc-url", "testro-jdbc-url"))   
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
		        .containsEntry("spring.datasource.replicated.rw.driver-class-name", "org.postgresql.Driver")
		        .containsEntry("spring.datasource.replicated.rw.password", "testrw-password")
		        .containsEntry("spring.datasource.replicated.rw.url", "testrw-jdbc-url")
		        .containsEntry("spring.datasource.replicated.rw.username", "testrw-username")
		        .containsEntry("spring.datasource.replicated.ro.driver-class-name", "org.postgresql.Driver")
		        .containsEntry("spring.datasource.replicated.ro.password", "testro-password")
		        .containsEntry("spring.datasource.replicated.ro.url", "testro-jdbc-url")
		        .containsEntry("spring.datasource.replicated.ro.username", "testro-username")
                .containsEntry("spring.datasource.replicated.name", "dbrw");
    } 
    
    @Test
    @DisplayName("composes r2dbc url from host port and database")
    void testR2dbc() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
        .containsEntry("spring.r2dbc.replicated.rw.password", "testrw-password")
        .containsEntry("spring.r2dbc.replicated.rw.url", "r2dbc:postgresql://testrw-host:testrw-port/testrw-database")
        .containsEntry("spring.r2dbc.replicated.rw.username", "testrw-username")
        .containsEntry("spring.r2dbc.replicated.ro.password", "testro-password")
        .containsEntry("spring.r2dbc.replicated.ro.url", "r2dbc:postgresql://testro-host:testro-port/testro-database")
        .containsEntry("spring.r2dbc.replicated.ro.username", "testro-username")                
        .containsEntry("spring.r2dbc.replicated.name", "dbrw");
    }   
    
    @Test
    @DisplayName("gives precedence to r2dbc-url")
    void testR2dbcURL() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret.withEntry("rw-r2dbc-url", "testrw-r2dbc-url").withEntry("ro-r2dbc-url", "testro-r2dbc-url"))     		
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
        .containsEntry("spring.r2dbc.replicated.rw.password", "testrw-password")
        .containsEntry("spring.r2dbc.replicated.rw.url", "testrw-r2dbc-url")
        .containsEntry("spring.r2dbc.replicated.rw.username", "testrw-username")
        .containsEntry("spring.r2dbc.replicated.ro.password", "testro-password")
        .containsEntry("spring.r2dbc.replicated.ro.url", "testro-r2dbc-url")
        .containsEntry("spring.r2dbc.replicated.ro.username", "testro-username")
        .containsEntry("spring.r2dbc.replicated.name", "dbrw");
    }    
    

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret)
        );
        environment.setProperty("org.springframework.cloud.bindings.boot.postgresql-replicated.enable", "false");

        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }    
    
    @Test
    @DisplayName("composes jdbc url from host port and database with sslmode and crdb option")
    void testJdbcWithSsl() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret.withEntry("rw-sslmode", "verify-full")
                		.withEntry("rw-sslrootcert", "root.cert")
                		.withEntry("rw-options", "--cluster=routing-id&opt=val1"))
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated.rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated.rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated.rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database?sslmode=verify-full&sslrootcert=dbrw-path/root.cert&options=--cluster=routing-id -c opt=val1")
                .containsEntry("spring.datasource.replicated.rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated.name", "dbrw");
    }
    
    @Test
    @DisplayName("composes jdbc url from host port and database with sslmode and crdb option")
    void testJdbcWithInvalidCrdbOption() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret.withEntry("rw-sslmode", "verify-full")
                		.withEntry("rw-sslrootcert", "root.cert")
                		.withEntry("rw-options", "-cluster=routing-id&opt=val1"))
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated.rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated.rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated.rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database?sslmode=verify-full&sslrootcert=dbrw-path/root.cert&options=-c -cluster=routing-id -c opt=val1")
                .containsEntry("spring.datasource.replicated.rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated.name", "dbrw");
    }    
    
    @Test
    @DisplayName("composes r2dbc url from host port and database with sslmode and crdb option")
    void testR2dbcWithSsl() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret.withEntry("rw-sslmode", "verify-full")
                		.withEntry("rw-sslrootcert", "root.cert")
                		.withEntry("rw-options", "--cluster=routing-id&opt=val1"))
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.r2dbc.replicated.rw.password", "testrw-password")
                .containsEntry("spring.r2dbc.replicated.rw.url", "r2dbc:postgresql://testrw-host:testrw-port/testrw-database?sslmode=verify-full&sslrootcert=dbrw-path/root.cert&options=--cluster=routing-id -c opt=val1")
                .containsEntry("spring.r2dbc.replicated.rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated.name", "dbrw");
    }   
    
    @Test
    @DisplayName("composes jdbc url from host port and database with sslmode disable")
    void testJdbcWithSslDisable() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret.withEntry("rw-sslmode", "disable"))
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated.rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated.rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated.rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database?sslmode=disable")
                .containsEntry("spring.datasource.replicated.rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated.name", "dbrw");
    }    
    
    @Test
    @DisplayName("composes jdbc url from host port and database with DB options")
    void testJdbcWithDBoptions() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret.withEntry("rw-options", "opt1=val1&opt2=val2"))
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated.rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated.rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated.rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database?options=-c opt1=val1 -c opt2=val2")
                .containsEntry("spring.datasource.replicated.rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated.name", "dbrw");
    }   
    
    @Test
    @DisplayName("composes jdbc url from host port and database with invalid DB options")
    void testJdbcWithInvaildDBoptions() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret.withEntry("rw-options", "opt1=val1&opt"))
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated.rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated.rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated.rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database?options=-c opt1=val1")
                .containsEntry("spring.datasource.replicated.rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated.name", "dbrw");
    }   
    
    @Test
    @DisplayName("composes jdbc url from host port and database with empty DB options")
    void testJdbcWithEmptyDBoptions() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret.withEntry("rw-options", ""))
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated.rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated.rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated.rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database")
                .containsEntry("spring.datasource.replicated.rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated.name", "dbrw");
    }
      
}
