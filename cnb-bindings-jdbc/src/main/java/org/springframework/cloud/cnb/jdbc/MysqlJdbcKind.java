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

import org.springframework.cloud.cnb.Binding;

public class MysqlJdbcKind implements JdbcKind{

    public static final String MYSQL_KIND = "mysql";
    public static final String MYSQL_SCHEME = "mysql";

    @Override
    public boolean forBinding(Binding binding) {
        return binding.getKind().equals(MYSQL_KIND);
    }

    @Override
    public String getScheme() {
        return MYSQL_SCHEME;
    }

    @Override
    public String getDriverClassName() {
        String driverClassNameToUse = null;
        try {
            driverClassNameToUse = "org.mariadb.jdbc.Driver";
            Class.forName(driverClassNameToUse, false, getClass().getClassLoader());
        } catch (ClassNotFoundException e) {
            try {
                driverClassNameToUse = "com.mysql.cj.jdbc.Driver";
                Class.forName(driverClassNameToUse, false, getClass().getClassLoader());
            } catch (ClassNotFoundException e2) {
                return null;
            }
        }
        return driverClassNameToUse;
    }
}
