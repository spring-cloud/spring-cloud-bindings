/*
 * Copyright 2019 the original author or authors.
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
package org.springframework.cloud.cnb.boot;

import org.springframework.cloud.cnb.core.Binding;

import java.util.Map;


public class CassandraCnbBindingProcessor implements CnbBindingProcessor {
    public static final String CASSANDRA_KIND = "cassandra";

    @Override
    public boolean accept(Binding binding) {
        return binding.getKind().equals(CASSANDRA_KIND);
    }

    @Override
    public void process(Binding binding, Map<String, Object> properties) {
        properties.put("spring.data.cassandra.username", binding.getSecret().get("username"));
        properties.put("spring.data.cassandra.password", binding.getSecret().get("password"));
        properties.put("spring.data.cassandra.contact-points", binding.getSecret().get("node_ips"));
        properties.put("spring.data.cassandra.port", binding.getSecret().get("port"));
    }

    @Override
    public CnbBindingProcessorProperties getProperties() {
        return null;
    }
}
