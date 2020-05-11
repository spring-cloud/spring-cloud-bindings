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

import org.springframework.cloud.bindings.Bindings;
import org.springframework.lang.NonNull;

import java.util.Map;

/**
 * An interface for types that transform the contents of a {@link Bindings} into properties for injection into a
 * {@link org.springframework.core.env.PropertySource}.
 */
@FunctionalInterface
public interface BindingsPropertiesProcessor {

    /**
     * Transform the currently accumulated {@link Bindings}-related properties.
     *
     * @param bindings   the {@code Bindings} exposed to the application.
     * @param properties the currently accumulated properties.
     */
    void process(@NonNull Bindings bindings, @NonNull Map<String, Object> properties);

}
