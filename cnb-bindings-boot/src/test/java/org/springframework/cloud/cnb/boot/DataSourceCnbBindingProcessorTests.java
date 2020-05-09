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

import org.springframework.cloud.cnb.core.Binding;


import static org.assertj.core.api.Assertions.assertThat;

public class DataSourceCnbBindingProcessorTests {

    @Test
    public void acceptIfJdbcTest() {
        DataSourceCnbBindingProcessor bindingProcessor = new DataSourceCnbBindingProcessor();
        Map<String, String> bindingMetadata = new HashMap<String, String>();
        bindingMetadata.put("kind", "mysql");
        Binding binding = new Binding(bindingMetadata, new HashMap<String, String>());
        assertThat(bindingProcessor.accept(binding)).isTrue();
    }

    @Test
    public void rejectIfNotJdbcTest() {
        DataSourceCnbBindingProcessor bindingProcessor = new DataSourceCnbBindingProcessor();
        Map<String, String> bindingMetadata = new HashMap<String, String>();
        bindingMetadata.put("kind", "redis");
        Binding binding = new Binding(bindingMetadata, new HashMap<String, String>());
        assertThat(bindingProcessor.accept(binding)).isFalse();
    }

    @Test
    public void processDataSourcePropertiesTest() {
        DataSourceCnbBindingProcessor bindingProcessor = new DataSourceCnbBindingProcessor();
        Map<String, String> bindingMetadata = new HashMap<String, String>();
        bindingMetadata.put("kind", "test-kind");
        Map<String,String> bindingSecret = new HashMap<String,String>();
        bindingSecret.put("hostname", "10.0.4.35");
        bindingSecret.put("port", "3306");
        bindingSecret.put("db", "some-db");
        bindingSecret.put("username", "some-username");
        bindingSecret.put("password", "some-password");
        Binding binding = new Binding(bindingMetadata, bindingSecret);
        Map<String,Object> properties = new HashMap<String,Object>();
        bindingProcessor.process(binding, properties);
        assertThat(properties.get("spring.datasource.url")).isEqualTo("jdbc:testscheme://10.0.4.35:3306/some-db?user=some-username&password=some-password");
        assertThat(properties.get("spring.datasource.username")).isEqualTo("some-username");
        assertThat(properties.get("spring.datasource.password")).isEqualTo("some-password");
        assertThat(properties.get("spring.datasource.driver-class-name")).isEqualTo("test.kind.fake.jdbc.Driver");
    }

    @Test
    public void processorPropertiesTest() {
        DataSourceCnbBindingProcessor bindingProcessor = new DataSourceCnbBindingProcessor();
        CnbBindingProcessorProperties processorProperties = bindingProcessor.getProperties();
    }

}
