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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import org.springframework.cloud.cnb.core.CnbBinding;

import static org.assertj.core.api.Assertions.assertThat;


public class JdbcUrlCreatorTest {

    @Test
    public void testJdbcUrlCreator_mysql() {
        CnbBinding mysqlBinding = bindingWithKind("mysql");
        JdbcUrlCreator jdbcUrlCreator = new JdbcUrlCreator(mysqlBinding);
        assertThat(jdbcUrlCreator.getJdbcUrl()).
                isEqualTo("jdbc:mysql://10.0.4.35:3306/some-db?user=some-username&password=some-password");
    }

    @Test
    public void testJdbcUrlCreator_db2() {
        CnbBinding db2Binding = bindingWithKind("db2");
        JdbcUrlCreator jdbcUrlCreator = new JdbcUrlCreator(db2Binding);

        assertThat(jdbcUrlCreator.getJdbcUrl()).
                isEqualTo("jdbc:db2://10.0.4.35:3306/some-db?user=some-username&password=some-password");
        assertThat(jdbcUrlCreator.getDriverClassName()).
                isEqualTo("com.ibm.db2.jcc.DB2Driver");
    }

    @Test
    public void testJdbcUrlCreator_oracle() {
        CnbBinding oracleBinding = bindingWithKind("oracle");
        JdbcUrlCreator jdbcUrlCreator = new JdbcUrlCreator(oracleBinding);
        assertThat(jdbcUrlCreator.getJdbcUrl())
                .isEqualTo("jdbc:oracle://10.0.4.35:3306/some-db?user=some-username&password=some-password");
        assertThat(jdbcUrlCreator.getDriverClassName()).
                isEqualTo("oracle.jdbc.OracleDriver");
    }

    @Test
    public void testJdbcUrlCreator_postgres() {
        CnbBinding oracleBinding = bindingWithKind("postgres");
        JdbcUrlCreator jdbcUrlCreator = new JdbcUrlCreator(oracleBinding);
        assertThat(jdbcUrlCreator.getJdbcUrl())
                .isEqualTo("jdbc:postgres://10.0.4.35:3306/some-db?user=some-username&password=some-password");
        assertThat(jdbcUrlCreator.getDriverClassName()).
                isEqualTo("org.postgresql.Driver");
    }

    @Test
    public void testJdbcUrlCreator_postgresql() {
        CnbBinding oracleBinding = bindingWithKind("postgresql");
        JdbcUrlCreator jdbcUrlCreator = new JdbcUrlCreator(oracleBinding);
        assertThat(jdbcUrlCreator.getJdbcUrl())
                .isEqualTo("jdbc:postgres://10.0.4.35:3306/some-db?user=some-username&password=some-password");
        assertThat(jdbcUrlCreator.getDriverClassName()).
                isEqualTo("org.postgresql.Driver");
    }

    @Test
    public void testJdbcUrlCreator_sqlserver() {
        CnbBinding oracleBinding = bindingWithKind("sqlserver");
        JdbcUrlCreator jdbcUrlCreator = new JdbcUrlCreator(oracleBinding);
        assertThat(jdbcUrlCreator.getJdbcUrl())
                .isEqualTo("jdbc:sqlserver://10.0.4.35:3306/some-db?user=some-username&password=some-password");
        assertThat(jdbcUrlCreator.getDriverClassName()).
                isEqualTo("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    public CnbBinding bindingWithKind(String kind) {
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("kind", kind);
        Map<String, String> secret = new HashMap<String, String>();
        secret.put("hostname", "10.0.4.35");
        secret.put("port", "3306");
        secret.put("db", "some-db");
        secret.put("username", "some-username");
        secret.put("password", "some-password");
        return new CnbBinding(metadata, secret);
    }
}
