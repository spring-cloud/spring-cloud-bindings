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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.bindings.boot.Guards.isGlobalEnabled;
import static org.springframework.core.env.CommandLinePropertySource.COMMAND_LINE_PROPERTY_SOURCE_NAME;

/**
 * An implementation of {@link EnvironmentPostProcessor} that delegates properties generation from {@link Bindings}.
 * This implementation generates a single instance of {@code Bindings} and then calls all implementations of
 * {@link BindingsPropertiesProcessor} registered with {@link SpringFactoriesLoader} allowing them to generate any
 * properties from the contents of the {@code Bindings}.
 * <p>
 * Must be enabled by setting the {@code org.springframework.cloud.bindings.boot.enable} System Property to {@code true}.
 */
public final class BindingsEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    /**
     * The name of the {@link PropertySource} created by the {@code BindingsEnvironmentPostProcessor}: {@value}.
     */
    public static final String BINDINGS_PROPERTY_SOURCE_NAME = "cnbBindings";

    final List<BindingsPropertiesProcessor> processors;

    private final Log log = LogFactory.getLog(getClass());

    private final Bindings bindings;

    /**
     * Creates a new instance of {@code BindingsEnvironmentPostProcessor} using the {@link Bindings} available in the
     * environment and the {@link BindingsPropertiesProcessor}s registered with {@link SpringFactoriesLoader}.
     */
    public BindingsEnvironmentPostProcessor() {
        this.bindings = new Bindings();
        this.processors = SpringFactoriesLoader.
                loadFactories(BindingsPropertiesProcessor.class, getClass().getClassLoader());
        AnnotationAwareOrderComparator.sort(this.processors);
    }

    BindingsEnvironmentPostProcessor(@NotNull Bindings bindings,
                                     @NotNull BindingsPropertiesProcessor... processors) {

        this.bindings = bindings;
        this.processors = Arrays.asList(processors);
    }

    @Override
    public void postProcessEnvironment(@NotNull ConfigurableEnvironment environment,
                                       @NotNull SpringApplication application) {

        if (!isGlobalEnabled()) {
            return;
        }

        if (bindings.getBindings().isEmpty()) {
            log.debug("No CNB Bindings found. Skipping Environment post-processing.");
            return;
        }

        Map<String, Object> properties = new HashMap<>();
        for (BindingsPropertiesProcessor processor : processors) {
            processor.process(bindings, properties);
        }
        if (properties.isEmpty()) {
            log.debug("No properties set from CNB Bindings. Skipping PropertySource creation.");
            return;
        }

        log.info("Creating PropertySource from CNB Bindings");
        MutablePropertySources propertySources = environment.getPropertySources();
        MapPropertySource propertySource = new MapPropertySource(BINDINGS_PROPERTY_SOURCE_NAME, properties);

        if (propertySources.contains(COMMAND_LINE_PROPERTY_SOURCE_NAME)) {
            propertySources.addAfter(COMMAND_LINE_PROPERTY_SOURCE_NAME, propertySource);
        } else {
            propertySources.addFirst(propertySource);
        }
    }

    @Override
    public int getOrder() {
        // Before ConfigFileApplicationListener so values there can use values from {@link Bindings}.
        return ConfigFileApplicationListener.DEFAULT_ORDER - 1;
    }

}
