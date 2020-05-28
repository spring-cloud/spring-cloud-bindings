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

import static org.springframework.cloud.bindings.boot.Guards.isKindEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of kind: {@value KIND}.
 *
 * @see <a href="https://www.ibm.com/support/knowledgecenter/SSEPGG_11.5.0/com.ibm.db2.luw.apdv.java.doc/src/tpc/imjcc_r0052342.html">JDBC URL Format</a>
 */
public final class Db2BindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    /**
     * The {@link Binding} kind that this processor is interested in: {@value}.
     **/
    public static final String KIND = "DB2";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isKindEnabled(environment, KIND)) {
            return;
        }

        bindings.filterBindings(KIND).forEach(binding -> {
            MapMapper map = new MapMapper(binding.getSecret(), properties);

            //jdbc properties
            map.from("password").to("spring.datasource.password");
            map.from("host", "port", "database").to("spring.datasource.url",
                    (host, port, database) -> String.format("jdbc:db2://%s:%s/%s", host, port, database));
            map.from("username").to("spring.datasource.username");

            properties.put("spring.datasource.driver-class-name", "com.ibm.db2.jcc.DB2Driver");

            //r2dbc properties
            map.from("password").to("spring.r2dbc.password");
            map.from("host", "port", "database").to("spring.r2dbc.url",
                    (host, port, database) -> String.format("r2dbc:db2://%s:%s/%s", host, port, database));
            map.from("username").to("spring.r2dbc.username");
        });
    }

}
