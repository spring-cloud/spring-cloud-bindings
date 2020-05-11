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
import static org.springframework.cloud.bindings.boot.PutIfPresent.put;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of kind: {@value KIND}.
 */
public final class CassandraBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    /**
     * The {@link Binding} kind that this processor is interested in: {@value}.
     **/
    public static final String KIND = "Cassandra";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isKindEnabled(environment, KIND)) {
            return;
        }

        bindings.filterBindings(KIND).forEach(binding -> {
            Map<String, String> secret = binding.getSecret();

            put(properties, "spring.data.cassandra.cluster-name").ifPresent(secret, "cluster-name");
            put(properties, "spring.data.cassandra.compression").ifPresent(secret, "compression");
            put(properties, "spring.data.cassandra.contact-points").ifPresent(secret, "contact-points");
            put(properties, "spring.data.cassandra.keyspace-name").ifPresent(secret, "keyspace-name");
            put(properties, "spring.data.cassandra.password").ifPresent(secret, "password");
            put(properties, "spring.data.cassandra.port").ifPresent(secret, "port");
            put(properties, "spring.data.cassandra.ssl").ifPresent(secret, "ssl");
            put(properties, "spring.data.cassandra.username").ifPresent(secret, "username");
        });
    }

}
