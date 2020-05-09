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
package org.springframework.cloud.cnb.core;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class BindingsTests {

    @Test
    void constructFromNonExistentDirectory() {
        String path = "src/test/resources/non-existent";
        Bindings b = new Bindings(path);

        assertThat(b.findBindings()).isEmpty();
    }

    @Test
    void constructFromNonDirectory() throws IOException {
        String path = File.createTempFile("bindings", "").getPath();
        Bindings b = new Bindings(path);

        assertThat(b.findBindings()).isEmpty();
    }

    @Test
    void construct() {
        String path = "src/test/resources";
        Bindings b = new Bindings(path);

        assertThat(b.findBindings()).hasSize(2);
    }

    @Test
    void hasBindings() {
        assertThat(Bindings.hasBindings(Collections.emptyMap())).isFalse();
        assertThat(Bindings.hasBindings(Collections.singletonMap("CNB_BINDINGS", ""))).isTrue();
    }

    @Test
    void getAllBindings() {
        String path = "src/test/resources";
        Bindings b = new Bindings(path);

        assertThat(b.findBindings()).hasSize(2);
    }

    @Test
    void findBindingsByKind() {
        String path = "src/test/resources";
        Bindings b = new Bindings(path);

        assertThat(b.findBindings("test-kind-1", null)).containsExactly(new Binding());
    }

    @Test
    void findBindingsByProvider() {
        String path = "src/test/resources";
        Bindings b = new Bindings(path);

        assertThat(b.findBindings(null, "test-provider-1")).containsExactly(new Binding());
    }

}
