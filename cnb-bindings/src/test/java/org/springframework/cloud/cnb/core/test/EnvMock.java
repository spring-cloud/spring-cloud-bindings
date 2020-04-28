/*
 * Copyright 2019 the original author or authors.
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

package org.springframework.cloud.cnb.core.test;

import java.util.HashMap;
import java.util.Map;

import mockit.MockUp;

import org.springframework.cloud.cnb.core.CnbBindings;



/**
 * @author Emily Casey
 **/
public class EnvMock {
    private MockUp<?> mockUp;

    public EnvMock(String cnbBindingsPath) {

        Map<String, String> env = System.getenv();
        this.mockUp = new MockUp<System>() {
            @mockit.Mock
            public String getenv(String name) {
                if (name.equalsIgnoreCase(CnbBindings.CNB_BINDINGS)) {
                    return cnbBindingsPath;
                }
                return env.get(name);
            }

            @mockit.Mock
            public Map getenv() {
                Map<String, String> finalMap = new HashMap<>();
                finalMap.putAll(env);
                finalMap.put("CNB_BINDINGS", cnbBindingsPath);
                return finalMap;
            }
        };
    }
}
