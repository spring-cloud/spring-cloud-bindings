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
import java.util.function.Predicate;

final class MapMapper {

    private final Map<String, String> source;

    private final Map<String, Object> destination;

    MapMapper(Map<String, String> source, Map<String, Object> destination) {
        this.source = source;
        this.destination = destination;
    }

    Source from(String... keys) {
        return new SourceImpl(keys);
    }

    interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

    interface Source {
        void to(String key);

        void toIfAbsent(String key);

        void to(String key, Function<String, Object> function);

        void to(String key, TriFunction<String, String, String, Object> function);

        Source when(Predicate<Object> predicate);

    }

    final class SourceImpl implements Source {

        private final String[] keys;

        private SourceImpl(String[] keys) {
            this.keys = keys;
        }

        @Override
        public void to(String key) {
            to(key, v -> v);
        }

        @Override
        public void toIfAbsent(String key) {
            if (destination.containsKey(key)) {
                return;
            }
            to(key, v -> v);
        }

        @Override
        public void to(String key, Function<String, Object> function) {
            if (keys.length != 1) {
                throw new IllegalStateException(
                        String.format("source size %d cannot be transformed as one argument", keys.length));
            }

            if (!Arrays.stream(keys).allMatch(source::containsKey)) {
                return;
            }

            destination.put(key, function.apply(source.get(keys[0])));
        }

        @Override
        public void to(String key, TriFunction<String, String, String, Object> function) {
            if (keys.length != 3) {
                throw new IllegalStateException(
                        String.format("source size %d cannot be consumed as three arguments", keys.length));
            }

            if (!Arrays.stream(keys).allMatch(source::containsKey)) {
                return;
            }

            destination.put(key, function.apply(source.get(keys[0]), source.get(keys[1]), source.get(keys[2])));
        }

        @Override
        public Source when(Predicate<Object> predicate) {
            if (keys.length != 1) {
                throw new IllegalStateException(
                        String.format("source size %d cannot be transformed as one argument", keys.length));
            }

            if (predicate.test(source.get(keys[0]))) {
                return this;
            } else {
                return new NoopSource();
            }
        }
    }

    final static class NoopSource implements Source {

        @Override
        public void to(String key) {

        }

        @Override
        public void toIfAbsent(String key) {

        }

        @Override
        public void to(String key, Function<String, Object> function) {

        }

        @Override
        public void to(String key, TriFunction<String, String, String, Object> function) {

        }

        @Override
        public Source when(Predicate<Object> predicate) {
            return this;
        }
    }

}
