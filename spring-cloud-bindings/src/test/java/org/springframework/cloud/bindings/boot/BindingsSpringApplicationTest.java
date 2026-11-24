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

import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.FluentMap;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Bindings with a real SpringApplication")
final class BindingsSpringApplicationTest {

    private static final String APPLICATION_JSON = "spring.application.json";

    private final Bindings bindings = new Bindings(
            new Binding("my-redis", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry(Binding.TYPE, "redis")
                            .withEntry("host", "from-binding")
                            .withEntry("password", "binding-secret")));

    @AfterEach
    void clearJson() {
        System.clearProperty(APPLICATION_JSON);
    }

    @Test
    @DisplayName("resolves binding placeholders in config files")
    void placeholderInConfigFile() {
        try (ConfigurableApplicationContext context = run()) {
            assertThat(context.getEnvironment().getProperty("app.password")).isEqualTo("binding-secret");
        }
    }

    @Test
    @DisplayName("binding overrides the config file")
    void bindingOverridesConfigFile() {
        try (ConfigurableApplicationContext context = run()) {
            assertThat(context.getEnvironment().getProperty("spring.data.redis.host")).isEqualTo("from-binding");
        }
    }

    @Test
    @DisplayName("command line overrides the binding")
    void commandLineOverridesBinding() {
        try (ConfigurableApplicationContext context = run("--spring.data.redis.host=cli")) {
            assertThat(context.getEnvironment().getProperty("spring.data.redis.host")).isEqualTo("cli");
        }
    }

    @Test
    @DisplayName("honours the global switch set in spring.application.json")
    void globalSwitchInJson() {
        System.setProperty(APPLICATION_JSON, "{\"org.springframework.cloud.bindings.boot.enable\":false}");

        try (ConfigurableApplicationContext context = run()) {
            assertThat(context.getEnvironment().getProperty("spring.data.redis.password")).isNull();
            assertThat(context.getEnvironment().getProperty("spring.data.redis.host")).isEqualTo("from-file");
        }
    }

    @Test
    @DisplayName("ranks spring.application.json above system properties")
    void jsonAboveSystemProperty() {
        System.setProperty("org.springframework.cloud.bindings.boot.enable", "true");
        System.setProperty(APPLICATION_JSON, "{\"org.springframework.cloud.bindings.boot.enable\":false}");

        try (ConfigurableApplicationContext context = run()) {
            assertThat(context.getEnvironment().getProperty("spring.data.redis.password")).isNull();
        } finally {
            System.clearProperty("org.springframework.cloud.bindings.boot.enable");
        }
    }

    @Test
    @DisplayName("ranks spring.application.json given as an argument above system properties")
    void argumentJsonAboveSystemProperty() {
        System.setProperty("org.springframework.cloud.bindings.boot.enable", "true");

        try (ConfigurableApplicationContext context = run(
                "--spring.application.json={\"org.springframework.cloud.bindings.boot.enable\":false}")) {
            assertThat(context.getEnvironment().getProperty("spring.data.redis.password")).isNull();
        } finally {
            System.clearProperty("org.springframework.cloud.bindings.boot.enable");
        }
    }

    @Test
    @DisplayName("honours a switch whose key differs in case")
    void relaxedSwitchKey() {
        try (ConfigurableApplicationContext context = run("--org.springframework.cloud.bindings.boot.ENABLE=false")) {
            assertThat(context.getEnvironment().getProperty("spring.data.redis.password")).isNull();
        }
    }

    @Test
    @DisplayName("honours a type switch with a dash in the JSON key")
    void relaxedTypeSwitchKeyInJson() {
        System.setProperty(APPLICATION_JSON, "{\"org.springframework.cloud.bindings.boot.re-dis.Enable\":false}");

        try (ConfigurableApplicationContext context = run()) {
            assertThat(context.getEnvironment().getProperty("spring.data.redis.password")).isNull();
        }
    }

    @Test
    @DisplayName("honours a type switch set in spring.application.json")
    void typeSwitchInJson() {
        System.setProperty(APPLICATION_JSON, "{\"org.springframework.cloud.bindings.boot.redis.enable\":false}");

        try (ConfigurableApplicationContext context = run()) {
            assertThat(context.getEnvironment().getProperty("spring.data.redis.password")).isNull();
        }
    }

    @Test
    @DisplayName("applies binding-specific properties when no switch is set")
    void enabledByDefault() {
        try (ConfigurableApplicationContext context = run()) {
            ConfigurableEnvironment environment = context.getEnvironment();
            assertThat(environment.getProperty("spring.data.redis.password")).isEqualTo("binding-secret");
            assertThat(environment.getProperty("k8s.bindings.my-redis.host")).isEqualTo("from-binding");
        }
    }

    private ConfigurableApplicationContext run(String... args) {
        String[] allArgs = new String[args.length + 1];
        allArgs[0] = "--spring.config.name=bindings-e2e";
        System.arraycopy(args, 0, allArgs, 1, args.length);

        return new SpringApplicationBuilder(TestConfiguration.class)
                .web(WebApplicationType.NONE)
                .listeners(
                        new BindingFlattenedEnvironmentPostProcessor(bindings),
                        new BindingSpecificEnvironmentPostProcessor(bindings, new RedisBindingsPropertiesProcessor()))
                .run(allArgs);
    }

    @Configuration(proxyBeanMethods = false)
    static class TestConfiguration {

    }

}
