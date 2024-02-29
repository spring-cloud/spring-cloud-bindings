/*
 * Copyright 2024 the original author or authors.
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

import java.util.Map;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.cloud.bindings.Binding;
import org.springframework.context.ApplicationListener;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of type: {@value TYPE}.
 */
public final class MySqlReplicatedBindingsPropertiesProcessor extends AbstractReplicatedDataSource
   implements ApplicationListener<ApplicationPreparedEvent>  {

	/**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "mysql-replicated";	
	
    private static final DeferredLog LOG = new DeferredLog();	
	
    /**
     * MySQL connection protocol constant.
     */
    private static final String MYSQL_PROTOCOL = "mysql";
    
    /**
     *  MariaDB connection protocol constant. 
     */
    private static final String MARIADB_PROTOCOL = "mariadb";    
    
    @Override
	protected void buildProperties(MapMapper map, Map<String, Object> properties,  Binding binding, String fnc)
	{
    
        //jdbc properties
        map.from(fnc + "-username").to(formatJDBCTemplate(fnc, "username"));
        map.from(fnc + "-password").to(formatJDBCTemplate(fnc, "password"));
        map.from(fnc + "-host", fnc + "-port", fnc + "-database").to(formatJDBCTemplate(fnc, "url"),
        		(host, port, database) -> String.format("jdbc:%s://%s:%s/%s", evalProtocol(), host, port, database));

        // jdbcURL takes precedence
        map.from(fnc + "-jdbc-url").to(formatJDBCTemplate(fnc, "url"));

        properties.put(formatJDBCTemplate(fnc, "driver-class-name"), "com.ibm.db2.jcc.DB2Driver");
        try {
            Class.forName("org.mariadb.jdbc.Driver", false, getClass().getClassLoader());
            properties.put(formatJDBCTemplate(fnc, "driver-class-name"), "org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver", false, getClass().getClassLoader());
                properties.put(formatJDBCTemplate(fnc, "driver-class-name"), "com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException ignored) {
            }
        }
        
        
        //r2dbc properties
        map.from(fnc + "-username").to(formatR2DBCTemplate(fnc, "username"));
        map.from(fnc + "-password").to(formatR2DBCTemplate(fnc, "password"));
        map.from(fnc + "-host", fnc + "-port", fnc + "-database").to(formatR2DBCTemplate(fnc, "url"),
                (host, port, database) -> String.format("r2dbc:%s://%s:%s/%s", evalProtocol(), host, port, database));

        // r2dbcURL takes precedence
        map.from(fnc + "-r2dbc-url").to(formatR2DBCTemplate(fnc, "url"));
    }    
   
	@Override
	protected String getType() {
		return TYPE;
	}
	
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        LOG.replayTo(getClass());
    }    
    
    private String evalProtocol()
    {
    	// Default to "mysql"
    	String connectionProtocol = MYSQL_PROTOCOL;
    	
    	/* Starting with Spring Boot 2.7.0, the previous MySQL r2dbc driver is no longer supported and 
    	 * documentation suggests using the MariaDB R2DBC driver as an alternative.  Some versions
    	 * of the MariaDB R2DBC driver do not support "mysql" as part of the connection
    	 * protocol; "mariadb" should be used instead when the MariaDB R2DBC driver class is on
    	 * the classpath.
    	 */
    	
    	try {
    		Class.forName("org.mariadb.r2dbc.MariadbConnection");
    		connectionProtocol = MARIADB_PROTOCOL;
    	}
    	catch (ClassNotFoundException ignored) {
        }
    	
    	return connectionProtocol; 
    }    
}
