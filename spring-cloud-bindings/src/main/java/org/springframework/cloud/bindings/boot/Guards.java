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

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.source.ConfigurationProperty;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

final class Guards {

    /** Name of the source Spring Boot attaches on top of all others. It wraps them, so ranking skips it. */
    private static final String ATTACHED_PROPERTY_SOURCE_NAME = "configurationProperties";

    private static final String GLOBAL_KEY = "org.springframework.cloud.bindings.boot.enable";

    private static final String TYPE_KEY_FORMAT = "org.springframework.cloud.bindings.boot.%s.enable";

    static boolean isGlobalEnabled(Environment environment) {
        return isEnabled(environment, GLOBAL_KEY);
    }

    static boolean isTypeEnabled(Environment environment, String type) {
        return isEnabled(environment, String.format(TYPE_KEY_FORMAT, type));
    }

    /**
     * The listeners run before Spring Boot unpacks {@code spring.application.json}, so a switch set there is not yet
     * in the {@link Environment}. Spring Boot ranks that JSON just above the system properties, so look at it at that
     * position among the property sources.
     */
    private static boolean isEnabled(Environment environment, String key) {
        Boolean value = resolve(environment, key);
        return value == null || value;
    }

    private static Boolean resolve(Environment environment, String key) {
        if (!(environment instanceof ConfigurableEnvironment configurable)) {
            Boolean value = environment.getProperty(key, Boolean.class);
            return value != null ? value : fromApplicationJson(environment, key);
        }

        boolean jsonChecked = false;
        for (PropertySource<?> source : configurable.getPropertySources()) {
            if (ATTACHED_PROPERTY_SOURCE_NAME.equals(source.getName())) {
                continue;
            }
            if (!jsonChecked && StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME.equals(source.getName())) {
                jsonChecked = true;
                Boolean value = fromApplicationJson(environment, key);
                if (value != null) {
                    return value;
                }
            }
            Object raw = lookup(source, key);
            if (raw != null) {
                return toBoolean(environment, raw);
            }
        }
        return jsonChecked ? null : fromApplicationJson(environment, key);
    }

    private static Boolean fromApplicationJson(Environment environment, String key) {
        String json = environment.getProperty("spring.application.json");
        if (json == null) {
            json = environment.getProperty("SPRING_APPLICATION_JSON");
        }
        if (json == null || json.isBlank()) {
            return null;
        }
        Map<String, Object> flattened;
        try {
            flattened = flatten(JsonParserFactory.getJsonParser().parseMap(json));
        } catch (RuntimeException e) {
            return null;
        }
        Object value = lookup(new MapPropertySource("applicationJson", flattened), key);
        return value == null ? null : toBoolean(environment, value);
    }

    /**
     * Looks the key up with the relaxed matching of Spring Boot's configuration properties, as the attached
     * {@code configurationProperties} source does for the whole environment.
     */
    private static Object lookup(PropertySource<?> source, String key) {
        ConfigurationPropertyName name = ConfigurationPropertyName.adapt(key, '.');
        for (ConfigurationPropertySource candidate : ConfigurationPropertySources.from(source)) {
            ConfigurationProperty property = candidate.getConfigurationProperty(name);
            if (property != null) {
                return property.getValue();
            }
        }
        return null;
    }

    private static Boolean toBoolean(Environment environment, Object value) {
        if (value instanceof String string) {
            value = environment.resolvePlaceholders(string);
        }
        return DefaultConversionService.getSharedInstance().convert(value, Boolean.class);
    }

    private static Map<String, Object> flatten(Map<String, Object> map) {
        Map<String, Object> result = new LinkedHashMap<>();
        flatten("", map, result);
        return result;
    }

    @SuppressWarnings("unchecked")
    private static void flatten(String prefix, Map<String, Object> source, Map<String, Object> result) {
        source.forEach((k, v) -> {
            String name = prefix.isEmpty() ? k : prefix + "." + k;
            if (v instanceof Map<?, ?> nested) {
                flatten(name, (Map<String, Object>) nested, result);
            } else {
                result.put(name, v);
            }
        });
    }

}
