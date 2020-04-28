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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.springframework.cloud.cnb.core.CnbBinding;


import static org.assertj.core.api.Assertions.assertThat;

public class CassandraCnbBindingProcessorTests {

    @Test
    public void acceptIfCassandraKind() {
        CassandraCnbBindingProcessor bindingProcessor = new CassandraCnbBindingProcessor();
        Map<String, String> bindingMetadata = new HashMap<String, String>();
        bindingMetadata.put("kind", "cassandra");
        CnbBinding binding = new CnbBinding(bindingMetadata, new HashMap<String, String>());
        assertThat(bindingProcessor.accept(binding)).isTrue();
    }

    @Test
    public void rejectIfNotCassandraKind() {
        CassandraCnbBindingProcessor bindingProcessor = new CassandraCnbBindingProcessor();
        Map<String, String> bindingMetadata = new HashMap<String, String>();
        bindingMetadata.put("kind", "mysql");
        CnbBinding binding = new CnbBinding(bindingMetadata, new HashMap<String, String>());
        assertThat(bindingProcessor.accept(binding)).isFalse();
    }

    @Test
    public void processDataSourcePropertiesTest() {
        CassandraCnbBindingProcessor bindingProcessor = new CassandraCnbBindingProcessor();
        Map<String, String> bindingMetadata = new HashMap<String, String>();
        bindingMetadata.put("kind", "cassandra");
        Map<String,String> bindingSecret = new HashMap<String,String>();
        bindingSecret.put("port", "9042");
        bindingSecret.put("node_ips", "10.0.4.35,10.0.4.36");
        bindingSecret.put("password", "some-password");
        bindingSecret.put("username", "some-username");
        CnbBinding binding = new CnbBinding(bindingMetadata, bindingSecret);
        Map<String,Object> properties = new HashMap<String,Object>();
        bindingProcessor.process(binding, properties);
        assertThat(properties.get("spring.data.cassandra.username")).isEqualTo("some-username");
        assertThat(properties.get("spring.data.cassandra.password")).isEqualTo("some-password");
        assertThat(properties.get("spring.data.cassandra.contact-points")).isEqualTo("10.0.4.35,10.0.4.36");
        assertThat(properties.get("spring.data.cassandra.port")).isEqualTo("9042");
    }

}
