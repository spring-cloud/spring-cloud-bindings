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

import java.util.Map;

final class PutIfPresent {

    private final Map<String, Object> destination;

    private final String key;

    PutIfPresent(Map<String, Object> destination, String key) {
        this.destination = destination;
        this.key = key;
    }

    static PutIfPresent put(Map<String, Object> destination, String key) {
        return new PutIfPresent(destination, key);
    }

    void ifPresent(Map<String, String> source, String key) {
        if (!source.containsKey(key)) {
            return;
        }

        destination.put(this.key, source.get(key));
    }

}
