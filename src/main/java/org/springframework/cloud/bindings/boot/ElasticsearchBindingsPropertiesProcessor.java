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
 */
final class ElasticsearchBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    /**
     * The {@link Binding} kind that this processor is interested in: {@value}.
     **/
    public static final String KIND = "Elasticsearch";


    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isKindEnabled(environment, KIND)) {
            return;
        }

        bindings.filterBindings(KIND).forEach(binding -> {
            MapMapper map = new MapMapper(binding.getSecret(), properties);

            map.from("endpoints").to("spring.data.elasticsearch.client.reactive.endpoints");
            map.from("password").to("spring.data.elasticsearch.client.reactive.password");
            map.from("use-ssl").to("spring.data.elasticsearch.client.reactive.use-ssl");
            map.from("username").to("spring.data.elasticsearch.client.reactive.username");
            map.from("password").to("spring.elasticsearch.jest.password");
            map.from("proxy.host").to("spring.elasticsearch.jest.proxy.host");
            map.from("proxy.port").to("spring.elasticsearch.jest.proxy.port");
            map.from("username").to("spring.elasticsearch.jest.username");
            map.from("password").to("spring.elasticsearch.rest.password");
            map.from("uris").to("spring.elasticsearch.rest.uris");
            map.from("username").to("spring.elasticsearch.rest.username");
        });
    }

}
