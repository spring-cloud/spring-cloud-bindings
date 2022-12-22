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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.cloud.bindings.boot.PropertySourceContributor.contributePropertySource;

/**
 * An implementation of {@link EnvironmentPostProcessor} that generates properties from {@link Bindings} with a
 * flattened format: {@code k8s.bindings.{name}.*}.
 */
public final class BindingFlattenedEnvironmentPostProcessor implements ApplicationListener<ApplicationPreparedEvent>,
        EnvironmentPostProcessor, Ordered {

    public static final String BINDING_FLATTENED_PROPERTY_SOURCE_NAME = "kubernetesServiceBindingFlattened";

    private final DeferredLog log = new DeferredLog();

    private final Bindings bindings;

    /**
     * Creates a new instance of {@code BindingFlattenedEnvironmentPostProcessor} using the {@link Bindings} available
     * in the environment.
     */
    public BindingFlattenedEnvironmentPostProcessor() {
        this(new Bindings());
    }

    BindingFlattenedEnvironmentPostProcessor(Bindings bindings) {
        this.bindings = bindings;
    }

    @Override
    public int getOrder() {
        // Before ConfigDataEnvironmentPostProcessor so values there can use values from {@link Bindings}.
        return ConfigDataEnvironmentPostProcessor.ORDER - 1;
    }

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        this.log.replayTo(getClass());
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> properties = new HashMap<>();
        bindings.getBindings().forEach(binding -> {
            binding.getSecret().forEach((key, value) -> {
                properties.put(String.format("k8s.bindings.%s.%s", binding.getName(), key), value);
            });
        });

        if (properties.isEmpty()) {
            log.debug("No properties set from Kubernetes Service Bindings. Skipping PropertySource creation.");
            return;
        }

        log.info("Creating flattened PropertySource from Kubernetes Service Bindings");
        contributePropertySource(BINDING_FLATTENED_PROPERTY_SOURCE_NAME, properties, environment);
    }

}
