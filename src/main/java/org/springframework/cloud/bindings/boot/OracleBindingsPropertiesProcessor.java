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

import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.core.env.Environment;

import java.util.Map;

import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of type: {@value TYPE}.
 *
 * @see <a href="https://docs.oracle.com/cd/E11882_01/appdev.112/e13995/oracle/jdbc/OracleDriver.html">JDBC URL Format</a>
 */
public final class OracleBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    /**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "oracle";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isTypeEnabled(environment, TYPE)) {
            return;
        }

        bindings.filterBindings(TYPE).forEach(binding -> {
            MapMapper map = new MapMapper(binding.getSecret(), properties);

            //jdbc properties
            map.from("username").to("spring.datasource.username");
            map.from("password").to("spring.datasource.password");
            map.from("host", "port", "database").to("spring.datasource.url",
                    (host, port, database) -> String.format("jdbc:oracle://%s:%s/%s", host, port, database));

            // jdbcURL takes precedence
            map.from("jdbc-url").to("spring.datasource.url");

            properties.put("spring.datasource.driver-class-name", "oracle.jdbc.OracleDriver");

            //r2dbc properties
            map.from("username").to("spring.r2dbc.username");
            map.from("password").to("spring.r2dbc.password");
            map.from("host", "port", "database").to("spring.r2dbc.url",
                    (host, port, database) -> String.format("r2dbc:oracle://%s:%s/%s", host, port, database));

            // r2dbcURL takes precedence
            map.from("r2dbc-url").to("spring.r2dbc.url");
        });
    }

}
