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

import org.springframework.boot.json.JsonParserFactory;
import org.springframework.core.env.Environment;

final class Guards {

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
     * in the {@link Environment}. Read it from the JSON when the switch is not set directly.
     */
    private static boolean isEnabled(Environment environment, String key) {
        Boolean value = environment.getProperty(key, Boolean.class);
        if (value == null) {
            value = fromApplicationJson(environment, key);
        }
        return value == null || value;
    }

    private static Boolean fromApplicationJson(Environment environment, String key) {
        String json = environment.getProperty("spring.application.json");
        if (json == null) {
            json = environment.getProperty("SPRING_APPLICATION_JSON");
        }
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            Object value = flatten(JsonParserFactory.getJsonParser().parseMap(json)).get(key);
            return value == null ? null : Boolean.valueOf(value.toString().trim());
        } catch (RuntimeException e) {
            return null;
        }
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
