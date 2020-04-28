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
package org.springframework.cloud.cnb.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides access to CNB Bindings.
 *
 * @author Emily Casey
 */
public class CnbBindings {

    public static final String CNB_BINDINGS = "CNB_BINDINGS";

    private List<CnbBinding> bindings = new ArrayList<>();

    public CnbBindings() {
        String cnbBindingsDir = System.getenv(CNB_BINDINGS);
        if (cnbBindingsDir == null) {
            return;
        }
        File bindingsDir = new File(cnbBindingsDir);
        if (!bindingsDir.exists()) {
            return;
        }
        if (!bindingsDir.isDirectory()) {
            throw new IllegalStateException(String.format("CNB_BINDINGS '%s' is not a directory", bindingsDir.toString()));
        }
        for (File file : bindingsDir.listFiles()) {
            if (!file.isDirectory()) {
                continue;
            }
            this.bindings.add(new CnbBinding(file));
        }
    }

    public List<CnbBinding> findAllBindings() {
        return this.bindings;
    }

    public boolean hasBindings() {
        return System.getenv(CNB_BINDINGS) != null;
    }

    public CnbBinding findBindingByName(String name) {
        for (CnbBinding binding : this.bindings) {
            if (binding.getName().equals(name)) {
                return binding;
            }
        }
        return null;
    }
}
