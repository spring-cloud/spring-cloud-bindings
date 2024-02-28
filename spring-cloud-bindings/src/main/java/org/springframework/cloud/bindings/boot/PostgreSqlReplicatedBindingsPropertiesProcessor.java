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

import java.util.Map;

import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of type: {@value TYPE}.
 */
public final class PostgreSqlReplicatedBindingsPropertiesProcessor extends AbstractPostgreSQLBindingsPropertiesProcessor 
   implements BindingsPropertiesProcessor, ApplicationListener<ApplicationPreparedEvent> {

	/**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "postgresql-replicated";
	
    private static final DeferredLog LOG = new DeferredLog();
    
    /**
     * Read/write function field
     */
    private static final String RW_FUNCTION = "rw";
    
    /**
     * Read only function field
     */
    private static final String RO_FUNCTION = "ro";
    
    /**
     * Template for a base JDBC properties
     */
    private static final String JDBC_BASE_TEMPLATE = "spring.datasource.replicated.%s";
    
    /**
     * Template for a base R2DBC properties
     */
    private static final String R2DBC_BASE_TEMPLATE = "spring.r2dbc.replicated.%s";
    
    /**
     * Template for function based JDBC properties
     */    
    private static final String JDBC_PROPERTY_TEMPLATE = "spring.datasource.replicated.%s.%s";

    /**
     * Template for function based R2DBC properties
     */     
    private static final String R2DBC_PROPERTY_TEMPLATE = "spring.r2dbc.replicated.%s.%s";		
    
    private String mode = RW_FUNCTION;
    
	@Override
	public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {

        if (!isTypeEnabled(environment, TYPE)) {
            return;
        }

        bindings.filterBindings(TYPE).forEach(binding -> {
         	
         	properties.put(String.format(JDBC_BASE_TEMPLATE, "name"), binding.getName());

         	properties.put(String.format(R2DBC_BASE_TEMPLATE, "name"), binding.getName());
         	
         	final var map = new MapMapper(binding.getSecret(), properties);
         	
        	mode = RW_FUNCTION;
         	buildProperties(map, properties, binding, RW_FUNCTION);
         	
         	mode = RO_FUNCTION;
         	buildProperties(map, properties, binding, RO_FUNCTION);
             	
        });
	}	
	
	private void buildProperties(MapMapper map, Map<String, Object> properties,  Binding binding, String fnc)
	{
        map.from(fnc + "-password").to(formatJDBCTemplate(fnc, "password"));
        
        map.from(fnc + "-host", fnc + "-port", fnc + "-database").to(formatJDBCTemplate(fnc, "url"),
                (host, port, database) -> String.format("jdbc:postgresql://%s:%s/%s", host, port, database));

        String sslParam = buildSslModeParam(binding);
        String sslModeOptions = buildDbOptions(binding);
        if (!"".equals(sslParam) && !"".equals(sslModeOptions)) {
            sslModeOptions = sslParam + "&" + sslModeOptions;
        } else if (!"".equals(sslParam) ) {
            sslModeOptions = sslParam;
        }

        if (!"".equals(sslModeOptions)) {
            properties.put(formatJDBCTemplate(fnc, "url") , 
            		properties.get(formatJDBCTemplate(fnc, "url")) + "?" + sslModeOptions);
        }
        map.from(fnc + "-username").to(formatJDBCTemplate(fnc, "username"));

        // jdbcURL takes precedence
        map.from(fnc + "-jdbc-url").to(formatJDBCTemplate(fnc, "url"));

        properties.put(formatJDBCTemplate(fnc, "driver-class-name"), "org.postgresql.Driver");

        //r2dbc properties
        map.from(fnc + "-password").to(formatR2DBCTemplate(fnc, "password"));
        map.from(fnc + "-host", fnc + "-port", fnc + "-database").to(formatR2DBCTemplate(fnc, "url"),
                (host, port, database) -> String.format("r2dbc:postgresql://%s:%s/%s", host, port, database));
        if (!"".equals(sslModeOptions)) {
            properties.put(formatR2DBCTemplate(fnc, "url") , 
            		properties.get(formatR2DBCTemplate(fnc, "url")) + "?" + sslModeOptions);
        }
        map.from(fnc + "-username").to(formatR2DBCTemplate(fnc, "username"));

        // r2dbcURL takes precedence
        map.from(fnc + "-r2dbc-url").to(formatR2DBCTemplate(fnc, "url"));
	}
	
	private String formatJDBCTemplate(String fnc, String field)
	{
		return String.format(JDBC_PROPERTY_TEMPLATE, fnc, field);
	}
	
	private String formatR2DBCTemplate(String fnc, String field)
	{
		return String.format(R2DBC_PROPERTY_TEMPLATE, fnc, field);
	}
	
	@Override
    protected String getDBOptionSecretField()
    {
    	return String.format("%s-%s", mode , OPTIONS);
    }
    
	@Override
    protected String getSSLModeSecretField()
    {
    	return String.format("%s-%s", mode , SSL_MODE);
    }
    
	@Override
    protected String getSSLRootCertSecretField()
    {
    	return String.format("%s-%s", mode , SSL_ROOT_CERT);
    }
	
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        LOG.replayTo(getClass());
    }
}
