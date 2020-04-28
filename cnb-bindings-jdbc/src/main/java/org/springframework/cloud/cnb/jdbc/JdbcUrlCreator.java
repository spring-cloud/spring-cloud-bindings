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
package org.springframework.cloud.cnb.jdbc;

import java.util.ServiceLoader;

import org.springframework.cloud.cnb.core.CnbBinding;
import org.springframework.cloud.cnb.core.IllegalBindingException;


public class JdbcUrlCreator {
    private static final String JDBC_PREFIX = "jdbc:";
    private final CnbBinding binding;
    private final JdbcKind kind;

    public static boolean isJDCBBinding(CnbBinding binding) {
        ServiceLoader<JdbcKind> loader = ServiceLoader.load(JdbcKind.class);
        for (JdbcKind kind : loader) {
            if (kind.forBinding(binding)) {
                return true;
            }
        }
        return false;
    }

    public JdbcUrlCreator(CnbBinding binding) {
        ServiceLoader<JdbcKind> loader = ServiceLoader.load(JdbcKind.class);
        JdbcKind jdbcKind = null;
        for (JdbcKind kind : loader) {
            if (kind.forBinding(binding)) {
                jdbcKind = kind;
                break;
            }
        }
        if (jdbcKind == null) {
            throw new IllegalBindingException("no matching jdbc kind for binding");
        }
        this.kind = jdbcKind;
        this.binding = binding;
    }

    public String getJdbcUrl() {
        return String.format("%s%s://%s/%s%s", JDBC_PREFIX, kind.getScheme(),
                buildHost(),
                buildPath(),
                buildQuery()
        );
    }

    public String getDriverClassName() {
        return kind.getDriverClassName();
    }

    private String buildHost() {
        return String.format("%s:%s",
                binding.getSecret().get("hostname"),
                binding.getSecret().get("port")
        );
    }

    private String buildPath() {
        return binding.getSecret().get("db");
    }

    private String buildQuery() {
        return String.format("?user=%s&password=%s",
                binding.getSecret().get("username"),
                binding.getSecret().get("password")
        );
    }

    public String getUsername() {
        return binding.getSecret().get("username");
    }

    public String getPassword() {
        return binding.getSecret().get("password");
    }
}
