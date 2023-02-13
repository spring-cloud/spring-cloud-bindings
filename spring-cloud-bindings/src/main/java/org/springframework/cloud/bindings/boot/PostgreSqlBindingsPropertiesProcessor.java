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

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of type: {@value TYPE}.
 *
 * @see <a href="https://jdbc.postgresql.org/documentation/80/connect.html">JDBC URL Format</a>
 */
public final class PostgreSqlBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    /**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "postgresql";
    /**
     * sslmode determines whether or with what priority a secure SSL TCP/IP connection will be negotiated with the server.
     */
    public static final String SSL_MODE = "sslmode";
    /**
     * sslrootcert specifies the name of a file containing SSL certificate authority (CA) certificate(s).
     */
    public static final String SSL_ROOT_CERT = "sslrootcert";
    /**
     * options Specifies command-line options to send to the server at connection start.
     * CockroachDB uses this to pass in cluster routing id
     */
    public static final String OPTIONS = "options";
    public static final String SPRING_DATASOURCE_URL = "spring.datasource.url";
    public static final String SPRING_R2DBC_URL = "spring.r2dbc.url";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isTypeEnabled(environment, TYPE)) {
            return;
        }

        bindings.filterBindings(TYPE).forEach(binding -> {
            MapMapper map = new MapMapper(binding.getSecret(), properties);

            //jdbc properties
            map.from("password").to("spring.datasource.password");
            map.from("host", "port", "database").to(SPRING_DATASOURCE_URL,
                    (host, port, database) -> String.format("jdbc:postgresql://%s:%s/%s", host, port, database));

            String sslParam = buildSslModeParam(binding);
            String dbOptions = buildDbOptions(binding);
            String sslModeOptions = dbOptions;
            if (!"".equals(sslParam) && !"".equals(sslModeOptions)) {
                sslModeOptions = sslParam + "&" + sslModeOptions;
            } else if (!"".equals(sslParam) ) {
                sslModeOptions = sslParam;
            }

            if (!"".equals(sslModeOptions)) {
                properties.put(SPRING_DATASOURCE_URL, properties.get(SPRING_DATASOURCE_URL) + "?" + sslModeOptions);
            }
            map.from("username").to("spring.datasource.username");

            // jdbcURL takes precedence
            map.from("jdbc-url").to("spring.datasource.url");

            properties.put("spring.datasource.driver-class-name", "org.postgresql.Driver");

            //r2dbc properties
            map.from("password").to("spring.r2dbc.password");
            map.from("host", "port", "database").to(SPRING_R2DBC_URL,
                    (host, port, database) -> String.format("r2dbc:postgresql://%s:%s/%s", host, port, database));
            if (!"".equals(sslModeOptions)) {
                properties.put(SPRING_R2DBC_URL, properties.get(SPRING_R2DBC_URL) + "?" + sslModeOptions);
            }
            map.from("username").to("spring.r2dbc.username");

            // r2dbcURL takes precedence
            map.from("r2dbc-url").to("spring.r2dbc.url");
        });
    }

    /**
     * Returns a concatenated list of options parameters defined in the bound file `options` in the format specified in
     * <a href="https://www.postgresql.org/docs/14/libpq-connect.html">PostgreSQL Doc</a>.
     * <p>
     * CockroachDB, which shares the same 'postgresql://' protocol as PostgreSQL, has customized options to meet its
     * distributed database nature.
     * Refer to <a href="https://www.cockroachlabs.com/docs/v21.2/connection-parameters#additional-connection-parameters">Client Connection Parameters</a>.
     */
    private String buildDbOptions(Binding binding) {
        String options = binding.getSecret().getOrDefault(OPTIONS, "");
        String crdbOption = "";
        List<String> dbOptions = new ArrayList<>();
        if (!options.equals("")) {
            String[] allOpts = options.split("&");
            for (String o : allOpts) {
                String[] keyval = o.split("=");
                if (keyval.length != 2 || keyval[0].length() == 0 || keyval[1].length() == 0) {
                    continue;
                }
                if (keyval[0].equals("--cluster")) {
                    crdbOption = keyval[0] + "=" + keyval[1];
                } else {
                    dbOptions.add("-c " + keyval[0] + "=" + keyval[1]);
                }
            }
        }
        String combinedOptions = crdbOption;
        if (dbOptions.size() > 0) {
            String otherOpts = String.join(" ", dbOptions);
            if (!combinedOptions.equals("")) {
                combinedOptions = combinedOptions + " " + otherOpts;
            } else {
                combinedOptions = otherOpts;
            }
        }
        if (!"".equals(combinedOptions)) {
            combinedOptions = "options=" + combinedOptions;
        }
        return combinedOptions;
    }

    /**
     * Returns a concatenated string of all ssl parameters for enabling one-way TLS (PostgreSQL certifies itself)
     * Refer to <a href="https://www.postgresql.org/docs/14/libpq-connect.html">PostgreSQL Doc</a>
     */
    private String buildSslModeParam(Binding binding) {
        //process ssl params
        //https://www.postgresql.org/docs/14/libpq-connect.html
        String sslmode = binding.getSecret().getOrDefault(SSL_MODE, "");
        String sslRootCert = binding.getSecret().getOrDefault(SSL_ROOT_CERT, "");
        StringBuilder sslparam = new StringBuilder();
        if (!"".equals(sslmode)) {
            sslparam.append(SSL_MODE).append("=").append(sslmode);
        }
        if (!"".equals(sslRootCert)) {
            if (!"".equals(sslmode)) {
                sslparam.append("&");
            }
            sslparam.append(SSL_ROOT_CERT).append("=")
                    .append(binding.getPath()).append(FileSystems.getDefault().getSeparator())
                    .append(sslRootCert);
        }
        return sslparam.toString();
    }
}
