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

import java.util.*;

import static org.springframework.cloud.bindings.boot.Guards.isKindEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of kind: {@value KIND}.
 */
public final class SpringSecurityOAuth2BindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    /**
     * The {@link Binding} kind that this processor is interested in: {@value}.
     **/
    public static final String KIND = "OAuth2";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isKindEnabled(environment, KIND)) {
            return;
        }

        bindings.filterBindings(KIND).forEach(binding -> {
            MapMapper map = new MapMapper(binding.getSecret(), properties);
            String provider = binding.getProvider();
            String clientName = binding.getName();
            properties.put(String.format("spring.security.oauth2.client.registration.%s.provider", clientName), provider);
            map.from("client-id").to(String.format("spring.security.oauth2.client.registration.%s.client-id", clientName));
            map.from("client-secret").to(String.format("spring.security.oauth2.client.registration.%s.client-secret", clientName));
            map.from("issuer-uri").to(String.format("spring.security.oauth2.client.provider.%s.issuer-uri", provider));
            map.from("authorization-uri").to(String.format("spring.security.oauth2.client.provider.%s.authorization-uri", provider));
            map.from("token-uri").to(String.format("spring.security.oauth2.client.provider.%s.token-uri", provider));
            map.from("user-info-uri").to(String.format("spring.security.oauth2.client.provider.%s.user-info-uri", provider));
            map.from("user-info-authentication-method").to(String.format("spring.security.oauth2.client.provider.%s.user-info-authentication-method", provider));
            map.from("jwk-set-uri").to(String.format("spring.security.oauth2.client.provider.%s.jwk-set-uri", provider));
            map.from("user-name-attribute").to(String.format("spring.security.oauth2.client.provider.%s.user-name-attribute", provider));
        });
    }

}
