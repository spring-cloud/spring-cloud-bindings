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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.junit.Test;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.cnb.boot.test.EnvMock;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.CollectionUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class CnbBindingsPostProcessorTest {

    @Test
    public void testPostProcessEnvironment(){
        Path resourceDirectory = Paths.get("src", "test", "resources", "test-bindings");
        String bindingsDir = resourceDirectory.toFile().getAbsolutePath();

        new EnvMock(bindingsDir);

        CnbBindingsPostProcessor environmentPostProcessor = new CnbBindingsPostProcessor();

        environmentPostProcessor.postProcessEnvironment(getEnvironment(),
                null);
        assertThat(getEnvironment().getProperty("spring.datasource.url"))
                .isEqualTo("jdbc:mysql://10.0.4.35:3306/mysql_name?user=mysql_username&password=mysql_password");
        assertThat(
                getEnvironment().getProperty("spring.datasource.username"))
                .isEqualTo("mysql_username");
        assertThat(
                getEnvironment().getProperty("spring.datasource.password"))
                .isEqualTo("mysql_password");
    }

    @Test
    public void testNoBindingsPresent(){
        CnbBindingsPostProcessor environmentPostProcessor = new CnbBindingsPostProcessor();

        // doen't fail
        environmentPostProcessor.postProcessEnvironment(getEnvironment(),
                null);
    }

    public ConfigurableEnvironment getEnvironment() {
        return getEnvironment(null);
    }

    public ConfigurableEnvironment getEnvironment(Map<String, Object> properties) {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(TestApp.class)
                .web(WebApplicationType.NONE);
        if (!CollectionUtils.isEmpty(properties)) {
            builder.properties(properties);
        }
        builder.bannerMode(Banner.Mode.OFF);
        ConfigurableApplicationContext applicationContext = builder.run();
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        applicationContext.close();
        return environment;
    }

    @SpringBootApplication
    static class TestApp {
    }

}
