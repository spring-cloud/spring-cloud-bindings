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
 * @see <a href="https://docs.oracle.com/cd/E11882_01/appdev.112/e13995/oracle/jdbc/OracleDriver.html">JDBC URL Format</a>
 */
public final class OracleBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    /**
     * The {@link Binding} kind that this processor is interested in: {@value}.
     **/
    public static final String KIND = "Oracle";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isKindEnabled(environment, KIND)) {
            return;
        }

        bindings.filterBindings(KIND).forEach(binding -> {
            Map<String, String> secret = binding.getSecret();

            properties.put("spring.datasource.driver-class-name", "oracle.jdbc.OracleDriver");
            properties.put("spring.datasource.password", secret.get("password"));
            properties.put("spring.datasource.url", String.format("jdbc:oracle://%s:%s/%s",
                    secret.get("host"), secret.get("port"), secret.get("database")));
            properties.put("spring.datasource.username", secret.get("username"));
        });
    }

}
