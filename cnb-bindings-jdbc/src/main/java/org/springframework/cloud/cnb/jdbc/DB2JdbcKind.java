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

public class DB2JdbcKind implements JdbcKind{

    public static final String DB2_KIND = "db2";
    public static final String DB2_SCHEME = "db2";

    @Override
    public boolean forBinding(Binding binding) {
        return binding.getKind().equals(DB2_KIND);
    }

    @Override
    public String getScheme() {
        return DB2_SCHEME;
    }

    @Override
    public String getDriverClassName() {
        return "com.ibm.db2.jcc.DB2Driver";
    }
}
