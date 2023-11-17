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

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;

import java.util.*;

import javax.annotation.Nullable;

import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of type: {@value TYPE}.
 */
public final class SpringSecurityOAuth2BindingsPropertiesProcessor implements BindingsPropertiesProcessor, ApplicationListener<ApplicationPreparedEvent> {

    /**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "oauth2";

    private static final DeferredLog LOG = new DeferredLog();

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isTypeEnabled(environment, TYPE)) {
            return;
        }

        bindings.filterBindings(TYPE).forEach(binding -> {
            MapMapper map = new MapMapper(binding.getSecret(), properties);
            String provider = binding.getProvider();
            if (provider == null) {
                LOG.warn(String.format("Binding '%s' is missing required 'provider' and will not be processed.", binding.getName()));
                return;
            }
            String clientName = binding.getName();
            properties.put(String.format("spring.security.oauth2.client.registration.%s.provider", clientName), provider);
            map.from("client-id").to(String.format("spring.security.oauth2.client.registration.%s.client-id", clientName));
            map.from("client-secret").to(String.format("spring.security.oauth2.client.registration.%s.client-secret", clientName));
            map.from("client-authentication-method")
                    .to(String.format("spring.security.oauth2.client.registration.%s.client-authentication-method", clientName),
                            SpringSecurityOAuth2BindingsPropertiesProcessor::toBackwardsCompatibleClientAuthenticationMethod);
            map.from("authorization-grant-type").to(String.format("spring.security.oauth2.client.registration.%s.authorization-grant-type", clientName));
            map.from("authorization-grant-types")
                    .when(SpringSecurityOAuth2BindingsPropertiesProcessor::hasSingleValue)
                    .toIfAbsent(String.format("spring.security.oauth2.client.registration.%s.authorization-grant-type", clientName));
            map.from("redirect-uri").to(String.format("spring.security.oauth2.client.registration.%s.redirect-uri", clientName));
            map.from("redirect-uris")
                    .when(SpringSecurityOAuth2BindingsPropertiesProcessor::hasSingleValue)
                    .toIfAbsent(String.format("spring.security.oauth2.client.registration.%s.redirect-uri", clientName));
            map.from("scope").to(String.format("spring.security.oauth2.client.registration.%s.scope", clientName));
            map.from("client-name").to(String.format("spring.security.oauth2.client.registration.%s.client-name", clientName));
            map.from("issuer-uri").to(String.format("spring.security.oauth2.client.provider.%s.issuer-uri", provider));
            map.from("authorization-uri").to(String.format("spring.security.oauth2.client.provider.%s.authorization-uri", provider));
            map.from("token-uri").to(String.format("spring.security.oauth2.client.provider.%s.token-uri", provider));
            map.from("user-info-uri").to(String.format("spring.security.oauth2.client.provider.%s.user-info-uri", provider));
            map.from("user-info-authentication-method").to(String.format("spring.security.oauth2.client.provider.%s.user-info-authentication-method", provider));
            map.from("jwk-set-uri").to(String.format("spring.security.oauth2.client.provider.%s.jwk-set-uri", provider));
            map.from("user-name-attribute").to(String.format("spring.security.oauth2.client.provider.%s.user-name-attribute", provider));
        });
    }

    private static boolean hasSingleValue(@Nullable Object value) {
        return Optional.ofNullable(value)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(s -> s.split(","))
                .filter(r -> r.length == 1)
                .isPresent();
    }

    /**
     * Spring Security 5.5 has introduced ClientAuthenticationMethod.CLIENT_SECRET_BASIC and
     * ClientAuthenticationMethod.CLIENT_SECRET_POST to match with the OpenID specification, and marked BASIC and POST
     * as deprecated. In older version, where the CLIENT_SECRET_* versions do not exist, Boot creates a mapping to a
     * new ClientAuthenticationMethod("client_secret_basic") which is not recognized by Spring Security.
     * <p>
     * From Security 6 upwards (Boot 3+), "basic" and "post" have been removed.
     * <p>
     * This transforms "client_secret_basic" to "basic" and "client_secret_post" to "post", so that it works with every
     * Boot 2 version, even Boot < 2.5.
     *
     * @param clientAuthenticationMethod the base client authentication method
     * @return "basic" instead of "client_secret_basic", "post" instead of "client_secret_post", the input otherwise
     */
    @Nullable
    private static String toBackwardsCompatibleClientAuthenticationMethod(@Nullable String clientAuthenticationMethod) {
        if ("client_secret_basic".equalsIgnoreCase(clientAuthenticationMethod)) {
            return "basic";
        }
        if ("client_secret_post".equalsIgnoreCase(clientAuthenticationMethod)) {
            return "post";
        }
        return clientAuthenticationMethod;
    }

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        LOG.replayTo(getClass());
    }
}
