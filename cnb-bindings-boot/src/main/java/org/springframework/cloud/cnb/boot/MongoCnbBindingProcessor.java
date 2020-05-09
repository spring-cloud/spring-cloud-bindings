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

import java.util.Map;

import org.springframework.cloud.cnb.core.Binding;

public class MongoCnbBindingProcessor implements CnbBindingProcessor {
    private static final String MONGO_KIND = "mongodb";

    @Override
    public boolean accept(Binding binding) {
        return binding.getKind().equals(MONGO_KIND);
    }

    @Override
    public void process(Binding binding, Map<String, Object> properties) {
        properties.put("spring.redis.mongodb.uri", binding.getSecret().get("uri"));

        // TODO: build uri from discrete fields?
    }

    @Override
    public CnbBindingProcessorProperties getProperties() {
        return CnbBindingProcessorProperties.builder()
                .propertyPrefixes("spring.data.mongodb")
                .serviceName("MongoDB")
                .build();
    }
}
