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

public class RedisCnbBindingProcessorTests {

    @Test
    public void acceptIfRedisKind() {
        RedisCnbBindingProcessor bindingProcessor = new RedisCnbBindingProcessor();
        Map<String, String> bindingMetadata = new HashMap<String, String>();
        bindingMetadata.put("kind", "redis");
        Binding binding = new Binding(bindingMetadata, new HashMap<String, String>());
        assertThat(bindingProcessor.accept(binding)).isTrue();
    }

    @Test
    public void rejectIfNotRedisKind() {
        RedisCnbBindingProcessor bindingProcessor = new RedisCnbBindingProcessor();
        Map<String, String> bindingMetadata = new HashMap<String, String>();
        bindingMetadata.put("kind", "mysql");
        Binding binding = new Binding(bindingMetadata, new HashMap<String, String>());
        assertThat(bindingProcessor.accept(binding)).isFalse();
    }

    @Test
    public void processDataSourcePropertiesTest() {
        RedisCnbBindingProcessor bindingProcessor = new RedisCnbBindingProcessor();
        Map<String, String> bindingMetadata = new HashMap<String, String>();
        bindingMetadata.put("kind", "redis");
        Map<String,String> bindingSecret = new HashMap<String,String>();
        bindingSecret.put("hostname", "10.0.4.35");
        bindingSecret.put("port", "6379");
        bindingSecret.put("password", "some-password");
        Binding binding = new Binding(bindingMetadata, bindingSecret);
        Map<String,Object> properties = new HashMap<String,Object>();
        bindingProcessor.process(binding, properties);
        assertThat(properties.get("spring.redis.host")).isEqualTo("10.0.4.35");
        assertThat(properties.get("spring.redis.port")).isEqualTo("6379");
        assertThat(properties.get("spring.redis.password")).isEqualTo("some-password");
    }

    //TODO: TLS for redis

}
