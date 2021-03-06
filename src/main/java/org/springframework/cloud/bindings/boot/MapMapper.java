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
import java.util.Map;
import java.util.function.Function;

final class MapMapper {

    private final Map<String, String> source;

    private final Map<String, Object> destination;

    MapMapper(Map<String, String> source, Map<String, Object> destination) {
        this.source = source;
        this.destination = destination;
    }

    Source from(String... keys) {
        return new Source(keys);
    }

    interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    final class Source {

        private final String[] keys;

        private Source(String[] keys) {
            this.keys = keys;
        }

        void to(String key) {
            to(key, v -> v);
        }

        void to(String key, Function<String, Object> function) {
            if (keys.length != 1) {
                throw new IllegalStateException(
                        String.format("source size %d cannot be transformed as one argument", keys.length));
            }

            if (!Arrays.stream(keys).allMatch(source::containsKey)) {
                return;
            }

            destination.put(key, function.apply(source.get(keys[0])));
        }

        void to(String key, TriFunction<String, String, String, Object> function) {
            if (keys.length != 3) {
                throw new IllegalStateException(
                        String.format("source size %d cannot be consumed as three arguments", keys.length));
            }

            if (!Arrays.stream(keys).allMatch(source::containsKey)) {
                return;
            }

            destination.put(key, function.apply(source.get(keys[0]), source.get(keys[1]), source.get(keys[2])));
        }

    }

}
