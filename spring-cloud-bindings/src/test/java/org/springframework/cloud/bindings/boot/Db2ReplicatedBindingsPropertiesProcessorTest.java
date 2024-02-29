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
import static org.springframework.cloud.bindings.boot.Db2ReplicatedBindingsPropertiesProcessor.TYPE;

import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.FluentMap;
import org.springframework.mock.env.MockEnvironment;

@DisplayName("DB2Replicated BindingsPropertiesProcessor")
public class Db2ReplicatedBindingsPropertiesProcessorTest {
	
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
        new Db2ReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
        .containsEntry("spring.datasource.replicated.rw.driver-class-name", "com.ibm.db2.jcc.DB2Driver")
        .containsEntry("spring.datasource.replicated.rw.password", "testrw-password")
        .containsEntry("spring.datasource.replicated.rw.url", "jdbc:db2://testrw-host:testrw-port/testrw-database")
        .containsEntry("spring.datasource.replicated.rw.username", "testrw-username")
        .containsEntry("spring.datasource.replicated.ro.driver-class-name", "com.ibm.db2.jcc.DB2Driver")
        .containsEntry("spring.datasource.replicated.ro.password", "testro-password")
        .containsEntry("spring.datasource.replicated.ro.url", "jdbc:db2://testro-host:testro-port/testro-database")
        .containsEntry("spring.datasource.replicated.ro.username", "testro-username")                
        .containsEntry("spring.datasource.replicated.name", "dbrw");
    }
    
    @Test
    @DisplayName("gives precedence to jdbc-url")
    void testJdbcURL() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret.withEntry("rw-jdbc-url", "testrw-jdbc-url").withEntry("ro-jdbc-url", "testro-jdbc-url")) 
        );
        new Db2ReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
        .containsEntry("spring.datasource.replicated.rw.driver-class-name", "com.ibm.db2.jcc.DB2Driver")
        .containsEntry("spring.datasource.replicated.rw.password", "testrw-password")
        .containsEntry("spring.datasource.replicated.rw.url", "testrw-jdbc-url")
        .containsEntry("spring.datasource.replicated.rw.username", "testrw-username")
        .containsEntry("spring.datasource.replicated.ro.driver-class-name", "com.ibm.db2.jcc.DB2Driver")
        .containsEntry("spring.datasource.replicated.ro.password", "testro-password")
        .containsEntry("spring.datasource.replicated.ro.url", "testro-jdbc-url")
        .containsEntry("spring.datasource.replicated.ro.username", "testro-username")
        .containsEntry("spring.datasource.replicated.name", "dbrw");
    }  
    
    @Test
    @DisplayName("contributes r2dbc properties")
    void testR2dbc() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret)
        );
        new Db2ReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
        .containsEntry("spring.r2dbc.replicated.rw.password", "testrw-password")
        .containsEntry("spring.r2dbc.replicated.rw.url", "r2dbc:db2://testrw-host:testrw-port/testrw-database")
        .containsEntry("spring.r2dbc.replicated.rw.username", "testrw-username")
        .containsEntry("spring.r2dbc.replicated.ro.password", "testro-password")
        .containsEntry("spring.r2dbc.replicated.ro.url", "r2dbc:db2://testro-host:testro-port/testro-database")
        .containsEntry("spring.r2dbc.replicated.ro.username", "testro-username")                
        .containsEntry("spring.r2dbc.replicated.name", "dbrw");
    }   
    
    @Test
    @DisplayName("gives precedence to r2dbc-url")
    void testR2dbcURL() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), secret.withEntry("rw-r2dbc-url", "testrw-r2dbc-url").withEntry("ro-r2dbc-url", "testro-r2dbc-url"))    
        );
        new Db2ReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
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
        environment.setProperty("org.springframework.cloud.bindings.boot.db2-replicated.enable", "false");

        new Db2ReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }    
}
