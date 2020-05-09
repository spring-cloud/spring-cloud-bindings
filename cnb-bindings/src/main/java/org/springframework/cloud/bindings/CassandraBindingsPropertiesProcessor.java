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

package org.springframework.cloud.bindings;

import org.jetbrains.annotations.NotNull;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of kind: {@value KIND}.
 */
public final class CassandraBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    /**
     * The {@link Binding} kind that this processor is interested in: {@value}.
     **/
    public static final String KIND = "cassandra";

    @Override
    public void process(@NonNull Bindings bindings, @NotNull Map<String, Object> properties) {
        bindings.filterBindings(KIND).forEach(binding -> {
            Map<String, String> secret = binding.getSecret();

            properties.put("spring.data.cassandra.contact-points", secret.get("node_ips"));
            properties.put("spring.data.cassandra.password", secret.get("password"));
            properties.put("spring.data.cassandra.port", secret.get("port"));
            properties.put("spring.data.cassandra.username", secret.get("username"));
        });
    }

}
