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

import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.core.env.Environment;

import java.util.Map;

import static org.springframework.cloud.bindings.boot.Guards.isKindEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of kind: {@value KIND}.
 */
public final class VaultBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    /**
     * The {@link Binding} kind that this processor is interested in: {@value}.
     **/
    public static final String KIND = "Vault";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isKindEnabled(environment, KIND)) {
            return;
        }

        bindings.filterBindings(KIND).forEach(binding -> {
            MapMapper map = new MapMapper(binding.getSecret(), properties);
            map.from("uri").to("spring.cloud.vault.uri");
            map.from("namespace").to("spring.cloud.vault.namespace"); // vault enterprise feature

            String method = binding.getSecret().get("method");
            if (method == null) {
                return;
            }
            String authenticationMethod = method.toUpperCase();
            properties.put("spring.cloud.vault.authentication", authenticationMethod);
            switch (authenticationMethod) {
                case "TOKEN":
                case "CUBBYHOLE":
                    map.from("token").to("spring.cloud.vault.token");
                    break;
                case "APPROLE":
                    map.from("role-id").to("spring.cloud.vault.app-role.role-id");
                    map.from("secret-id").to("spring.cloud.vault.app-role.secret-id");
                    map.from("role").to("spring.cloud.vault.app-role.role");
                    map.from("app-role-path").to("spring.cloud.vault.app-role.app-role-path");
                    break;
                case "CERT":
                    properties.put("spring.cloud.vault.ssl.key-store", binding.getSecretFilePath("keystore.jks").toString());
                    map.from("key-store-password").to("spring.cloud.vault.ssl.key-store-password");
                    map.from("cert-auth-path").to("spring.cloud.vault.ssl.cert-auth-path");
            }
        });
    }

}
