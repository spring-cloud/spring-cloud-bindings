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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;


import static org.springframework.cloud.bindings.boot.Guards.isGlobalEnabled;
import static org.springframework.cloud.bindings.boot.PropertySourceContributor.contributePropertySource;

/**
 * An implementation of environment post-processor that delegates properties generation from {@link Bindings}.
 * This implementation generates a single instance of {@code Bindings} and then calls all implementations of
 * {@link BindingsPropertiesProcessor} registered with {@link SpringFactoriesLoader} allowing them to generate any
 * properties from the contents of the {@code Bindings}.
 * <p>
 * Must be enabled by setting the {@code org.springframework.cloud.bindings.boot.enable} System Property to
 * {@code true}.
 */
public final class BindingSpecificEnvironmentPostProcessor implements SmartApplicationListener {

    /**
     * The name of the {@link PropertySource} created by the {@code BindingsEnvironmentPostProcessor}: {@value}.
     */
    public static final String BINDING_SPECIFIC_PROPERTY_SOURCE_NAME = "kubernetesServiceBindingSpecific";

    private static final DeferredLog LOG = new DeferredLog();

    final List<BindingsPropertiesProcessor> processors;

    private final Bindings bindings;

    /**
     * Creates a new instance of {@code BindingSpecificEnvironmentPostProcessor} using the {@link Bindings} available in
     * the environment and the {@link BindingsPropertiesProcessor}s registered with {@link SpringFactoriesLoader}.
     */
    public BindingSpecificEnvironmentPostProcessor() {
        this.bindings = new Bindings();
        this.processors = SpringFactoriesLoader.
                loadFactories(BindingsPropertiesProcessor.class, getClass().getClassLoader());
    }

    BindingSpecificEnvironmentPostProcessor(Bindings bindings, BindingsPropertiesProcessor... processors) {
        this.bindings = bindings;
        this.processors = Arrays.asList(processors);
    }

    @Override
    public int getOrder() {
        // Runs before the listener that applies the EnvironmentPostProcessors (HIGHEST_PRECEDENCE + 10), so values in
        // config files can use values from {@link Bindings}. A listener, not an EnvironmentPostProcessor, because
        // that interface moved packages between Spring Boot 3 and 4.
        return Ordered.HIGHEST_PRECEDENCE + 9;
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationEnvironmentPreparedEvent.class.isAssignableFrom(eventType)
                || ApplicationPreparedEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationEnvironmentPreparedEvent prepared) {
            postProcessEnvironment(prepared.getEnvironment(), prepared.getSpringApplication());
        } else if (event instanceof ApplicationPreparedEvent) {
            LOG.replayTo(getClass());
        }
    }

    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (!isGlobalEnabled(environment)) {
            return;
        }

        if (bindings.getBindings().isEmpty()) {
            LOG.debug("No Kubernetes Service Bindings found. Skipping Environment post-processing.");
            return;
        }

        Map<String, Object> properties = new HashMap<>();
        processors.forEach(processor -> processor.process(environment, bindings, properties));
        if (properties.isEmpty()) {
            LOG.debug("No properties set from Kubernetes Service Bindings. Skipping PropertySource creation.");
            return;
        }

        LOG.info("Creating binding-specific PropertySource from Kubernetes Service Bindings");
        contributePropertySource(BINDING_SPECIFIC_PROPERTY_SOURCE_NAME, properties, environment);
    }

}
