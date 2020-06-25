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
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.FluentMap;
import org.springframework.mock.env.MockEnvironment;

import java.nio.file.Paths;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.SpringSecurityOAuth2BindingsPropertiesProcessor.KIND;

@DisplayName("Spring Security OAuth2 BindingsPropertiesProcessor")
final class SpringSecurityOAuth2BindingsPropertiesProcessorTest {

    private final Bindings bindings = new Bindings(
            new Binding("test-name-1", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry("kind", KIND)
                            .withEntry("provider", "github"),
                    new FluentMap()
                            .withEntry("client-id", "github-client-id")
                            .withEntry("client-secret", "github-client-secret")
            ),
            new Binding("test-name-2", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry("kind", KIND)
                            .withEntry("provider", "okta"),
                    new FluentMap()
                            .withEntry("client-id", "okta-client-id")
                            .withEntry("client-secret", "okta-client-secret")
                            .withEntry("issuer-uri", "okta-issuer-uri")
            ),
            new Binding("test-name-3", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry("kind", KIND)
                            .withEntry("provider", "my-provider"),
                    new FluentMap()
                            .withEntry("client-id", "my-provider-client-id")
                            .withEntry("client-secret", "my-provider-client-secret")
                            .withEntry("authorization-uri", "my-provider-authorization-uri")
                            .withEntry("token-uri", "my-provider-token-uri")
                            .withEntry("user-info-uri", "my-provider-user-info-uri")
                            .withEntry("user-info-authentication-method", "my-provider-user-info-authentication-method")
                            .withEntry("jwk-set-uri", "my-provider-jwk-set-uri")
                            .withEntry("user-name-attribute", "my-provider-user-name-attribute")
            ),
            // Don't crash when provider is missing
            new Binding("test-missing-provider", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry("kind", KIND),
                    new FluentMap()
                            .withEntry("client-id", "my-provider-client-id")
            )
    );

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("contributes client properties for common providers")
    void testCommonProvider() {
        new SpringSecurityOAuth2BindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.security.oauth2.client.registration.test-name-1.client-id", "github-client-id")
                .containsEntry("spring.security.oauth2.client.registration.test-name-1.client-secret", "github-client-secret")
                .containsEntry("spring.security.oauth2.client.registration.test-name-1.provider", "github")
        ;
    }

    @Test
    @DisplayName("contributes client properties for OIDC providers")
    void testOidcProvider() {
        new SpringSecurityOAuth2BindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.security.oauth2.client.registration.test-name-2.client-id", "okta-client-id")
                .containsEntry("spring.security.oauth2.client.registration.test-name-2.client-secret", "okta-client-secret")
                .containsEntry("spring.security.oauth2.client.registration.test-name-2.provider", "okta")
                .containsEntry("spring.security.oauth2.client.provider.okta.issuer-uri", "okta-issuer-uri")
        ;
    }

    @Test
    @DisplayName("contributes client properties for non-OIDC providers")
    void testProvider() {
        new SpringSecurityOAuth2BindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.security.oauth2.client.registration.test-name-3.client-id", "my-provider-client-id")
                .containsEntry("spring.security.oauth2.client.registration.test-name-3.client-secret", "my-provider-client-secret")
                .containsEntry("spring.security.oauth2.client.registration.test-name-3.provider", "my-provider")
                .containsEntry("spring.security.oauth2.client.provider.my-provider.authorization-uri", "my-provider-authorization-uri")
                .containsEntry("spring.security.oauth2.client.provider.my-provider.token-uri", "my-provider-token-uri")
                .containsEntry("spring.security.oauth2.client.provider.my-provider.user-info-uri", "my-provider-user-info-uri")
                .containsEntry("spring.security.oauth2.client.provider.my-provider.user-info-authentication-method", "my-provider-user-info-authentication-method")
                .containsEntry("spring.security.oauth2.client.provider.my-provider.jwk-set-uri", "my-provider-jwk-set-uri")
                .containsEntry("spring.security.oauth2.client.provider.my-provider.user-name-attribute", "my-provider-user-name-attribute")
        ;
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.oauth2.enable", "false");

        new SpringSecurityOAuth2BindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }

}
