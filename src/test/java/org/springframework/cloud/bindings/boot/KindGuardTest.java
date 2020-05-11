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
import org.junitpioneer.jupiter.SetSystemProperty;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.KindGuard.isKindEnabled;

@DisplayName("Kind Guard")
final class KindGuardTest {

    @Test
    @DisplayName("returns true if unset")
    void unset() {
        assertThat(isKindEnabled("Test")).isTrue();
    }

    @Test
    @DisplayName("returns the set value of true")
    @SetSystemProperty(key = "org.springframework.cloud.bindings.boot.test.enable", value = "true")
    void setTrue() {
        assertThat(isKindEnabled("Test")).isTrue();
    }

    @Test
    @DisplayName("returns the set value of false")
    @SetSystemProperty(key = "org.springframework.cloud.bindings.boot.test.enable", value = "false")
    void setFalse() {
        assertThat(isKindEnabled("Test")).isFalse();
    }

}
