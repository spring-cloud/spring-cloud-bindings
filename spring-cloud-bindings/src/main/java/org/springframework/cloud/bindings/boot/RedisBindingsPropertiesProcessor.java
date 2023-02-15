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
public final class RedisBindingsPropertiesProcessor {

    /**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "redis";

    /**
     * This is a special case for Boot 2.
     */
    public final static class Boot2 extends SpringBootVersionResolver implements BindingsPropertiesProcessor {

        private static final int BOOT_VERSION = 2;

        Boot2(int forcedVersion) {
            super(forcedVersion);
        }

        public Boot2() {
        }

        @Override
        public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
            if (!isTypeEnabled(environment, TYPE)) {
                return;
            }
            if (!isBootMajorVersionEnabled(BOOT_VERSION)) {
                return;
            }

            bindings.filterBindings(TYPE).forEach(binding -> {
                MapMapper map = new MapMapper(binding.getSecret(), properties);

                map.from("client-name").to("spring.redis.client-name");
                map.from("cluster.max-redirects").to("spring.redis.cluster.max-redirects");
                map.from("cluster.nodes").to("spring.redis.cluster.nodes");
                map.from("database").to("spring.redis.database");
                map.from("host").to("spring.redis.host");
                map.from("password").to("spring.redis.password");
                map.from("port").to("spring.redis.port");
                map.from("sentinel.master").to("spring.redis.sentinel.master");
                map.from("sentinel.nodes").to("spring.redis.sentinel.nodes");
                map.from("ssl").to("spring.redis.ssl");
                map.from("url").to("spring.redis.url");
            });
        }
    }

    /**
     * This is a special case for Boot 3.
     */
    public final static class Boot3 extends SpringBootVersionResolver implements BindingsPropertiesProcessor {

        private static final int BOOT_VERSION = 3;

        Boot3(int forcedVersion) {
            super(forcedVersion);
        }

        public Boot3() {
        }

        @Override
        public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
            if (!isTypeEnabled(environment, TYPE)) {
                return;
            }
            if (!isBootMajorVersionEnabled(BOOT_VERSION)) {
                return;
            }

            bindings.filterBindings(TYPE).forEach(binding -> {
                MapMapper map = new MapMapper(binding.getSecret(), properties);

                map.from("client-name").to("spring.data.redis.client-name");
                map.from("cluster.max-redirects").to("spring.data.redis.cluster.max-redirects");
                map.from("cluster.nodes").to("spring.data.redis.cluster.nodes");
                map.from("database").to("spring.data.redis.database");
                map.from("host").to("spring.data.redis.host");
                map.from("password").to("spring.data.redis.password");
                map.from("port").to("spring.data.redis.port");
                map.from("sentinel.master").to("spring.data.redis.sentinel.master");
                map.from("sentinel.nodes").to("spring.data.redis.sentinel.nodes");
                map.from("ssl").to("spring.data.redis.ssl");
                map.from("url").to("spring.data.redis.url");
            });
        }
    }

}
