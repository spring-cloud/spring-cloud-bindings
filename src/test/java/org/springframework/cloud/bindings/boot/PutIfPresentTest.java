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
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.PutIfPresent.put;

@DisplayName("Put if present")
final class PutIfPresentTest {

    @Test
    @DisplayName("puts if present")
    void present() {
        Map<String, String> source = Collections.singletonMap("test-source-key", "test-source-value");
        Map<String, Object> destination = new HashMap<>();

        put(destination, "test-destination-key").ifPresent(source, "test-source-key");

        assertThat(destination).containsEntry("test-destination-key", "test-source-value");
    }

    @Test
    @DisplayName("does not put if not present")
    void notPresent() {
        Map<String, String> source = Collections.emptyMap();
        Map<String, Object> destination = new HashMap<>();

        put(destination, "test-destination-key").ifPresent(source, "test-source-key");

        assertThat(destination).doesNotContainKey("test-destination-key");
    }

}
