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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.bindings.boot.Guards.isGlobalEnabled;
import static org.springframework.cloud.bindings.boot.PropertySourceContributor.contributePropertySource;

/**
 * An implementation of {@link EnvironmentPostProcessor} that delegates properties generation from {@link Bindings}.
 * This implementation generates a single instance of {@code Bindings} and then calls all implementations of
 * {@link BindingsPropertiesProcessor} registered with {@link SpringFactoriesLoader} allowing them to generate any
 * properties from the contents of the {@code Bindings}.
 * <p>
 * Must be enabled by setting the {@code org.springframework.cloud.bindings.boot.enable} System Property to
 * {@code true}.
 */
public final class BindingSpecificEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    /**
     * The name of the {@link PropertySource} created by the {@code BindingsEnvironmentPostProcessor}: {@value}.
     */
    public static final String BINDING_SPECIFIC_PROPERTY_SOURCE_NAME = "cnbBindingSpecific";

    final List<BindingsPropertiesProcessor> processors;

    private final Log log = LogFactory.getLog(getClass());

    private final Bindings bindings;

    /**
     * Creates a new instance of {@code BindingSpecificEnvironmentPostProcessor} using the {@link Bindings} available in
     * the environment and the {@link BindingsPropertiesProcessor}s registered with {@link SpringFactoriesLoader}.
     */
    public BindingSpecificEnvironmentPostProcessor() {
        this.bindings = new Bindings();
        this.processors = SpringFactoriesLoader.
                loadFactories(BindingsPropertiesProcessor.class, getClass().getClassLoader());
        AnnotationAwareOrderComparator.sort(this.processors);
    }

    BindingSpecificEnvironmentPostProcessor(Bindings bindings, BindingsPropertiesProcessor... processors) {
        this.bindings = bindings;
        this.processors = Arrays.asList(processors);
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!isGlobalEnabled()) {
            return;
        }

        if (bindings.getBindings().isEmpty()) {
            log.debug("No CNB Bindings found. Skipping Environment post-processing.");
            return;
        }

        Map<String, Object> properties = new HashMap<>();
        processors.forEach(processor -> processor.process(bindings, properties));
        if (properties.isEmpty()) {
            log.debug("No properties set from CNB Bindings. Skipping PropertySource creation.");
            return;
        }

        log.info("Creating binding-specific PropertySource from CNB Bindings");
        contributePropertySource(BINDING_SPECIFIC_PROPERTY_SOURCE_NAME, properties, environment);
    }

    @Override
    public int getOrder() {
        // Before ConfigFileApplicationListener so values there can use values from {@link Bindings}.
        return ConfigFileApplicationListener.DEFAULT_ORDER - 1;
    }

}
