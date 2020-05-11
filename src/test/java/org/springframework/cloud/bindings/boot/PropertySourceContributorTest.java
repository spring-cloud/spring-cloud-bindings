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
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.mock.env.MockEnvironment;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.PropertySourceContributor.contributePropertySource;

@DisplayName("PropertySource Contributor")
final class PropertySourceContributorTest {

    private final MockEnvironment environment = new MockEnvironment();

    @Test
    @DisplayName("adds PropertySource after CommandLinePropertySource")
    void withCommandLinePropertySource() {
        environment.getPropertySources().addFirst(new SimpleCommandLinePropertySource());

        contributePropertySource("test-name", Collections.singletonMap("test-key", "test-value"), environment);

        PropertySource<?> propertySource = environment.getPropertySources().get("test-name");
        assertThat(propertySource).isNotNull();
        assertThat(propertySource.getProperty("test-key")).isEqualTo("test-value");
        assertThat(environment.getPropertySources().precedenceOf(propertySource)).isEqualTo(1);
    }

    @Test
    @DisplayName("adds PropertySource first")
    void withoutCommandLinePropertySource() {
        contributePropertySource("test-name", Collections.singletonMap("test-key", "test-value"), environment);

        PropertySource<?> propertySource = environment.getPropertySources().get("test-name");
        assertThat(propertySource).isNotNull();
        assertThat(propertySource.getProperty("test-key")).isEqualTo("test-value");
        assertThat(environment.getPropertySources().precedenceOf(propertySource)).isEqualTo(0);
    }

}
