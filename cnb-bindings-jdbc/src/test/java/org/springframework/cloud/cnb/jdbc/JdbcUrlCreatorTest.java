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
    public void testJdbcUrlCreator() {
        Map<String,String> mysqlMetadata = new HashMap<String,String>();
        mysqlMetadata.put("kind", "mysql");
        Map<String,String> mysqlSecret = new HashMap<String,String>();
        mysqlSecret.put("hostname", "10.0.4.35");
        mysqlSecret.put("port", "3306");
        mysqlSecret.put("db", "some-db");
        mysqlSecret.put("username", "some-username");
        mysqlSecret.put("password", "some-password");
        CnbBinding mysqlBinding = new CnbBinding(mysqlMetadata, mysqlSecret);
        JdbcUrlCreator jdbcUrlCreator = new JdbcUrlCreator(mysqlBinding);
        String url = jdbcUrlCreator.getJdbcUrl();
        assertThat(url).isEqualTo("jdbc:mysql://10.0.4.35:3306/some-db?user=some-username&password=some-password");
    }
}
