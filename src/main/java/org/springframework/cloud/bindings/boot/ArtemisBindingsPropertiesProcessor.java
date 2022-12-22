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

import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of type: {@value TYPE}.
 */
public final class ArtemisBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    /**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "artemis";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isTypeEnabled(environment, TYPE)) {
            return;
        }

        bindings.filterBindings(TYPE).forEach(binding -> {
            MapMapper map = new MapMapper(binding.getSecret(), properties);

            map.from("broker-url").to("spring.artemis.broker-url");
            map.from("mode").to("spring.artemis.mode");
            map.from("password").to("spring.artemis.password");
            map.from("user").to("spring.artemis.user");

            map.from("embedded.cluster-password").to("spring.artemis.embedded.cluster-password");
            map.from("embedded.data-directory").to("spring.artemis.embedded.data-directory");
            map.from("embedded.enabled").to("spring.artemis.embedded.enabled");
            map.from("embedded.persistent").to("spring.artemis.embedded.persistent");
            map.from("embedded.queues").to("spring.artemis.embedded.queues");
            map.from("embedded.server-id").to("spring.artemis.embedded.server-id");
            map.from("embedded.topics").to("spring.artemis.embedded.topics");

            map.from("pool.block-if-full").to("spring.artemis.pool.block-if-full");
            map.from("pool.block-if-full-timeout").to("spring.artemis.pool.block-if-full-timeout");
            map.from("pool.enabled").to("spring.artemis.pool.enabled");
            map.from("pool.idle-timeout").to("spring.artemis.pool.idle-timeout");
            map.from("pool.max-connections").to("spring.artemis.pool.max-connections");
            map.from("pool.max-sessions-per-connection").to("spring.artemis.pool.max-sessions-per-connection");
            map.from("pool.time-between-expiration-check").to("spring.artemis.pool.time-between-expiration-check");
            map.from("pool.use-anonymous-producers").to("spring.artemis.pool.use-anonymous-producers");
        });
    }

}
