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
import org.junitpioneer.jupiter.SetSystemProperty;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.FluentMap;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.Db2BindingsPropertiesProcessor.KIND;

@DisplayName("DB2 BindingsPropertiesProcessor")
final class Db2BindingsPropertiesProcessorTest {

    private final Bindings bindings = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    Collections.singletonMap("kind", KIND),
                    new FluentMap()
                            .withEntry("database", "test-database")
                            .withEntry("host", "test-host")
                            .withEntry("password", "test-password")
                            .withEntry("port", "test-port")
                            .withEntry("username", "test-username")
            )
    );

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("contributes properties")
    void test() {
        new Db2BindingsPropertiesProcessor().process(bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.driver-class-name", "com.ibm.db2.jcc.DB2Driver")
                .containsEntry("spring.datasource.password", "test-password")
                .containsEntry("spring.datasource.url", "jdbc:db2://test-host:test-port/test-database")
                .containsEntry("spring.datasource.username", "test-username");
    }

    @Test
    @DisplayName("can be disabled")
    @SetSystemProperty(key = "org.springframework.cloud.bindings.boot.db2.enable", value = "false")
    void disabled() {
        new Db2BindingsPropertiesProcessor().process(bindings, properties);
        assertThat(properties).isEmpty();
    }

}
