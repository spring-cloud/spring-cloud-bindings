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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.FluentMap;
import org.springframework.mock.env.MockEnvironment;

import java.nio.file.Paths;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.CouchbaseBindingsPropertiesProcessor.TYPE;

@DisplayName("Couchbase BindingsPropertiesProcessor")
final class CouchbaseBindingsPropertiesProcessorTest {

    private final Bindings bindingsSpringBoot2 = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry(Binding.TYPE, TYPE)
                            .withEntry("bootstrap-hosts", "test-bootstrap-hosts")
                            .withEntry("bucket.name", "test-bucket-name")
                            .withEntry("bucket.password", "test-bucket-password")
                            .withEntry("env.bootstrap.http-direct-port", "test-env-bootstrap-http-direct-port")
                            .withEntry("env.bootstrap.http-ssl-port", "test-env-bootstrap-http-ssl-port")
                            .withEntry("password", "test-password")
                            .withEntry("username", "test-username")
            )
    );

    private final Bindings bindingsSpringBoot3 = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry(Binding.TYPE, TYPE)
                            .withEntry("connection-string", "test-connection-string")
                            .withEntry("bucket-name", "test-bucket-name")
                            .withEntry("username", "test-username")
                            .withEntry("password", "test-password")
            )
    );

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("contributes properties - Spring Boot 2 flavor")
    void testSb2() {
        new CouchbaseBindingsPropertiesProcessor.Boot2(2).process(environment, bindingsSpringBoot2, properties);
        assertThat(properties)
                .containsEntry("spring.couchbase.bootstrap-hosts", "test-bootstrap-hosts")
                .containsEntry("spring.couchbase.bucket.name", "test-bucket-name")
                .containsEntry("spring.couchbase.bucket.password", "test-bucket-password")
                .containsEntry("spring.couchbase.env.bootstrap.http-direct-port", "test-env-bootstrap-http-direct-port")
                .containsEntry("spring.couchbase.env.bootstrap.http-ssl-port", "test-env-bootstrap-http-ssl-port")
                .containsEntry("spring.couchbase.password", "test-password")
                .containsEntry("spring.couchbase.username", "test-username");
    }

    @Test
    @DisplayName("contributes properties - Spring Boot 3 flavor")
    void testSb3() {
        new CouchbaseBindingsPropertiesProcessor.Boot3(3).process(environment, bindingsSpringBoot3, properties);
        assertThat(properties)
                .containsEntry("spring.couchbase.connection-string", "test-connection-string")
                .containsEntry("spring.data.couchbase.bucket-name", "test-bucket-name")
                .containsEntry("spring.couchbase.username", "test-username")
                .containsEntry("spring.couchbase.password", "test-password");
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.couchbase.enable", "false");

        new CouchbaseBindingsPropertiesProcessor.Boot2(2).process(environment, bindingsSpringBoot2, properties);
        assertThat(properties).isEmpty();

        new CouchbaseBindingsPropertiesProcessor.Boot3(3).process(environment, bindingsSpringBoot3, properties);
        assertThat(properties).isEmpty();
    }

}
