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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.mock.env.MockEnvironment;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.cloud.bindings.boot.Guards.isGlobalEnabled;
import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

@DisplayName("Guards")
final class GuardsTest {

    @Nested
    @DisplayName("Global Guard")
    final class GlobalGuard {

        private final MockEnvironment environment = new MockEnvironment();

        @Test
        @DisplayName("returns true if unset")
        void unset() {
            assertThat(isGlobalEnabled(environment)).isTrue();
        }

        @Test
        @DisplayName("returns the set value of true")
        void setTrue() {
            environment.setProperty("org.springframework.cloud.bindings.boot.enable", "true");
            assertThat(isGlobalEnabled(environment)).isTrue();
        }

        @Test
        @DisplayName("returns the set value of false")
        void setFalse() {
            environment.setProperty("org.springframework.cloud.bindings.boot.enable", "false");
            assertThat(isGlobalEnabled(environment)).isFalse();
        }

    }

    @Nested
    @DisplayName("Type Guard")
    final class TypeGuard {

        private final MockEnvironment environment = new MockEnvironment();

        @Test
        @DisplayName("returns true if unset")
        void unset() {
            assertThat(isTypeEnabled(environment, "Test")).isTrue();
        }

        @Test
        @DisplayName("returns the set value of true")
        void setTrue() {
            environment.setProperty("org.springframework.cloud.bindings.boot.test.enable", "true");
            assertThat(isTypeEnabled(environment, "Test")).isTrue();
        }

        @Test
        @DisplayName("returns the set value of false")
        void setFalse() {
            environment.setProperty("org.springframework.cloud.bindings.boot.test.enable", "false");
            assertThat(isTypeEnabled(environment, "test")).isFalse();
        }

    }

    @Nested
    @DisplayName("Application JSON")
    final class ApplicationJson {

        private final MockEnvironment environment = new MockEnvironment();

        @Test
        @DisplayName("honours a flat global switch in spring.application.json")
        void globalFlat() {
            environment.setProperty("spring.application.json",
                    "{\"org.springframework.cloud.bindings.boot.enable\":false}");
            assertThat(isGlobalEnabled(environment)).isFalse();
        }

        @Test
        @DisplayName("honours a nested type switch in spring.application.json")
        void typeNested() {
            environment.setProperty("spring.application.json",
                    "{\"org\":{\"springframework\":{\"cloud\":{\"bindings\":{\"boot\":{\"redis\":"
                            + "{\"enable\":\"false\"}}}}}}}");
            assertThat(isTypeEnabled(environment, "redis")).isFalse();
            assertThat(isTypeEnabled(environment, "kafka")).isTrue();
        }

        @Test
        @DisplayName("honours SPRING_APPLICATION_JSON")
        void envVar() {
            environment.setProperty("SPRING_APPLICATION_JSON",
                    "{\"org.springframework.cloud.bindings.boot.enable\":false}");
            assertThat(isGlobalEnabled(environment)).isFalse();
        }

        @Test
        @DisplayName("prefers a directly set property over the JSON")
        void directWins() {
            environment.setProperty("org.springframework.cloud.bindings.boot.enable", "true");
            environment.setProperty("spring.application.json",
                    "{\"org.springframework.cloud.bindings.boot.enable\":false}");
            assertThat(isGlobalEnabled(environment)).isTrue();
        }

        @Test
        @DisplayName("treats yes, on and 1 as true and no, off and 0 as false")
        void truthyValues() {
            for (String value : new String[]{"yes", "on", "1", "TRUE"}) {
                environment.setProperty("spring.application.json",
                        "{\"org.springframework.cloud.bindings.boot.enable\":\"" + value + "\"}");
                assertThat(isGlobalEnabled(environment)).as(value).isTrue();
            }
            for (String value : new String[]{"no", "off", "0", "false"}) {
                environment.setProperty("spring.application.json",
                        "{\"org.springframework.cloud.bindings.boot.enable\":\"" + value + "\"}");
                assertThat(isGlobalEnabled(environment)).as(value).isFalse();
            }
        }

        @Test
        @DisplayName("ranks the JSON above system properties")
        void jsonAboveSystemProperties() {
            StandardEnvironment standard = new StandardEnvironment();
            standard.getPropertySources().replace("systemProperties", new MapPropertySource("systemProperties",
                    Map.of("org.springframework.cloud.bindings.boot.enable", "true",
                            "spring.application.json", "{\"org.springframework.cloud.bindings.boot.enable\":false}")));
            ConfigurationPropertySources.attach(standard);

            assertThat(isGlobalEnabled(standard)).isFalse();
        }

        @Test
        @DisplayName("ranks command line arguments above the JSON")
        void commandLineAboveJson() {
            StandardEnvironment standard = new StandardEnvironment();
            standard.getPropertySources().replace("systemProperties", new MapPropertySource("systemProperties",
                    Map.of("spring.application.json", "{\"org.springframework.cloud.bindings.boot.enable\":false}")));
            standard.getPropertySources().addFirst(new MapPropertySource("commandLineArgs",
                    Map.of("org.springframework.cloud.bindings.boot.enable", "true")));
            ConfigurationPropertySources.attach(standard);

            assertThat(isGlobalEnabled(standard)).isTrue();
        }

        @Test
        @DisplayName("reads an environment variable when there is no JSON")
        void environmentVariable() {
            StandardEnvironment standard = new StandardEnvironment();
            standard.getPropertySources().replace("systemEnvironment", new SystemEnvironmentPropertySource(
                    "systemEnvironment", Map.of("ORG_SPRINGFRAMEWORK_CLOUD_BINDINGS_BOOT_ENABLE", "false")));
            standard.getPropertySources().replace("systemProperties",
                    new MapPropertySource("systemProperties", Map.of()));

            assertThat(isGlobalEnabled(standard)).isFalse();
        }

        @Test
        @DisplayName("ranks the JSON above environment variables")
        void jsonAboveEnvironmentVariables() {
            StandardEnvironment standard = new StandardEnvironment();
            standard.getPropertySources().replace("systemEnvironment", new SystemEnvironmentPropertySource("systemEnvironment",
                    Map.of("ORG_SPRINGFRAMEWORK_CLOUD_BINDINGS_BOOT_ENABLE", "true")));
            standard.getPropertySources().replace("systemProperties", new MapPropertySource("systemProperties",
                    Map.of("spring.application.json", "{\"org.springframework.cloud.bindings.boot.enable\":false}")));

            assertThat(isGlobalEnabled(standard)).isFalse();
        }

        @Test
        @DisplayName("fails on a value that cannot be converted")
        void unconvertible() {
            for (String json : new String[]{"\"abc\"", "0", "1"}) {
                environment.setProperty("spring.application.json",
                        "{\"org.springframework.cloud.bindings.boot.enable\":" + json + "}");
                assertThatThrownBy(() -> isGlobalEnabled(environment)).as(json)
                        .isInstanceOf(ConversionException.class);
            }
        }

        @Test
        @DisplayName("fails on a null value")
        void nullValue() {
            environment.setProperty("spring.application.json", "{\"org.springframework.cloud.bindings.boot.enable\":null}");
            assertThatThrownBy(() -> isGlobalEnabled(environment)).isInstanceOf(RuntimeException.class);
        }

        @Test
        @DisplayName("ignores invalid JSON")
        void invalidJson() {
            environment.setProperty("spring.application.json", "{not json");
            assertThat(isGlobalEnabled(environment)).isTrue();
        }

        @Test
        @DisplayName("ignores JSON without the switch")
        void unrelatedJson() {
            environment.setProperty("spring.application.json", "{\"foo\":\"bar\"}");
            assertThat(isGlobalEnabled(environment)).isTrue();
        }

    }

}
