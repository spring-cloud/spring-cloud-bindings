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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import org.springframework.cloud.cnb.core.test.EnvMock;

import static org.assertj.core.api.Assertions.assertThat;

public class CnbBindingsTests {

    @Test
    public void testBindings() {
        Path resourceDirectory = Paths.get("src", "test", "resources", "test-bindings");
        String bindingsDir = resourceDirectory.toFile().getAbsolutePath();

        new EnvMock(bindingsDir);

        CnbBindings cnbBindings = new CnbBindings();

        List<CnbBinding> bindings = cnbBindings.findAllBindings();
        assertThat(bindings.size()).isEqualTo(1);

        CnbBinding mysqlBinding = cnbBindings.findBindingByName("p-mysql");
        Map<String, String> mysqlSecret = mysqlBinding.getSecret();
        assertThat(mysqlSecret).containsEntry("hostname", "10.0.4.35")
                .containsEntry("port", "3306")
                .containsEntry("name", "mysql_name")
                .containsEntry("username", "mysql_username")
                .containsEntry("password", "mysql_password")
                .containsEntry("uri",
                        "mysql://mysql_username:mysql_password@10.0.4.35:3306/cf_2e23d10a_8738_8c3c_66cf_13e44422698c?reconnect=true")
                .containsEntry("jdbcUrl",
                        "jdbc:mysql://10.0.4.35:3306/cf_2e23d10a_8738_8c3c_66cf_13e44422698c?user=mysql_username&password=mysql_password");
        Map<String, String> mysqlMetadata = mysqlBinding.getAllMetadata();
        assertThat(mysqlMetadata).containsEntry("kind", "mysql")
                .containsEntry("provider", "p-mysql")
                .containsEntry("tags", "mysql,relational");
        assertThat(mysqlBinding.getKind()).isEqualTo("mysql");
        assertThat(mysqlBinding.getProvider()).isEqualTo("p-mysql");
        assertThat(mysqlBinding.getTags()).contains("mysql", "relational");
    }

}
