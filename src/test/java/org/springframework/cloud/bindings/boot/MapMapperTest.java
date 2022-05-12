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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Map Mapper test")
final class MapMapperTest {

    private final Map<String, Object> destination = new HashMap<>();

    private final Map<String, String> source = new HashMap<>();

    private final MapMapper map = new MapMapper(source, destination);

    @Test
    @DisplayName("puts if present")
    void present() {
        source.put("test-source-key", "test-source-value");

        map.from("test-source-key").to("test-destination-key");

        assertThat(destination).containsEntry("test-destination-key", "test-source-value");
    }

    @Test
    @DisplayName("transforms source value")
    void transformed() {
        source.put("test-source-key", "test-source-value");

        map.from("test-source-key").to("test-destination-key", s -> {
            assertThat(s).isEqualTo("test-source-value");

            return "test-destination-value";
        });

        assertThat(destination).containsEntry("test-destination-key", "test-destination-value");
    }

    @Test
    @DisplayName("does not put if not present")
    void notPresent() {
        map.from("test-source-key").to("test-destination-key");

        assertThat(destination).doesNotContainKey("test-destination-key");
    }

    @Test
    @DisplayName("puts if all present")
    void allPresent() {
        source.put("test-source-key-1", "test-source-value-1");
        source.put("test-source-key-2", "test-source-value-2");
        source.put("test-source-key-3", "test-source-value-3");

        map.from("test-source-key-1", "test-source-key-2", "test-source-key-3").to("test-destination-key", (a, b, c) -> {
            assertThat(a).isEqualTo("test-source-value-1");
            assertThat(b).isEqualTo("test-source-value-2");
            assertThat(c).isEqualTo("test-source-value-3");

            return "test-destination-value";
        });

        assertThat(destination).containsEntry("test-destination-key", "test-destination-value");
    }

    @Test
    @DisplayName("does not put if not all present")
    void notAllPresent() {
        source.put("test-source-key-1", "test-source-value-1");
        source.put("test-source-key-2", "test-source-value-2");

        map.from("test-source-key-1", "test-source-key-2", "test-source-key-3").to("test-destination-key", (a, b, c) -> "test-destination-value");

        assertThat(destination).doesNotContainKey("test-destination-key");
    }

    @Nested
    class ToIfAbsentTests {
        @Test
        @DisplayName("puts if absent in destination")
        void absent() {
            source.put("test-source-key-1", "test-source-value-1");

            map.from("test-source-key-1").toIfAbsent("test-destination-key");

            assertThat(destination).containsEntry("test-destination-key", "test-source-value-1");
        }

        @Test
        @DisplayName("does not put if present in destination")
        void present() {
            source.put("test-source-key-1", "test-source-value-1");
            source.put("test-source-key-2", "test-source-value-2");

            map.from("test-source-key-1").to("test-destination-key");
            map.from("test-source-key-2").toIfAbsent("test-destination-key");

            assertThat(destination).containsEntry("test-destination-key", "test-source-value-1");
        }

    }

    @Nested
    class WhenTests {
        @Test
        @DisplayName("puts when predicate is true")
        void truePredicate() {
            source.put("test-source-key-1", "test-source-value-1");

            map.from("test-source-key-1").when(value -> true).to("test-destination-key");

            assertThat(destination).containsEntry("test-destination-key", "test-source-value-1");
        }

        @Test
        @DisplayName("does not put when predicate is false")
        void falsePredicate() {
            source.put("test-source-key-1", "test-source-value-1");

            map.from("test-source-key-1").when(value -> false).to("test-destination-key");

            assertThat(destination).doesNotContainKey("test-destination-key").doesNotContainValue("test-source-value-1");
        }

        @Test
        @DisplayName("does not put when the key is missing")
        void missingKey() {
            source.put("test-source-key-1", "test-source-value-1");

            map.from("missing-key").when(value -> true).to("test-destination-key");

            assertThat(destination).doesNotContainKey("test-destination-key");
        }

        @Test
        @DisplayName("only supports one key")
        void onlySupportsOneKey() {
            assertThatThrownBy(() -> map.from("one", "two", "three").when(value -> true))
                    .isInstanceOf(IllegalStateException.class);
        }

        @Test
        @DisplayName("is complete noop when predicate is false")
        void falsePredicateIsNoop() {
            source.put("test-source-key-1", "test-source-value-1");
            source.put("test-source-key-2", "test-source-value-2");
            source.put("test-source-key-3", "test-source-value-3");

            MapMapper.Source noopSource = map.from("test-source-key-1").when(value -> false);
            noopSource.to("one");
            noopSource.toIfAbsent("two");
            noopSource.to("three", str -> "content");
            noopSource.to("four", (a, b, c) -> "content");

            assertThat(destination).isEmpty();
        }

        @Test
        @DisplayName("noopSource.when with false predicate is idempotent")
        void isIdemPotentWhenPredicateIsFalse() {
            source.put("test-source-key-1", "test-source-value-1");

            MapMapper.Source noopSource = map.from("test-source-key-1").when(value -> false);

            assertThat(noopSource)
                    .isSameAs(noopSource.when(value -> true))
                    .isSameAs(noopSource.when(value -> false));
        }

    }

}
