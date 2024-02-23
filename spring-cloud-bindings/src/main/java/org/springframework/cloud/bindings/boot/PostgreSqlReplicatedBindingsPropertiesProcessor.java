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
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of type: {@value TYPE}.
 */
public class PostgreSqlReplicatedBindingsPropertiesProcessor extends AbstractPostgreSQLBindingsPropertiesProcessor implements ApplicationListener<ApplicationPreparedEvent> {

	/**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "postgresql-replicated";
	
    private static final DeferredLog LOG = new DeferredLog();
	
    private static final String CORRELATION_FIELD = "correlation";
    
    private static final String FUNCTION_FILED = "function";
    
    private static final String RW_FUNCTION = "rw";
    
    private static final String RO_FUNCTION = "ro";
    
    private static final String JDBC_BASE_TEMPLATE = "spring.datasource.replicated[%d].%s";
    
    private static final String R2DBC_BASE_TEMPLATE = "spring.r2dbc.replicated[%d].%s";
    
    private static final String JDBC_PROPERTY_TEMPLATE = "spring.datasource.replicated[%d].%s.%s";

    private static final String R2DBC_PROPERTY_TEMPLATE = "spring.r2dbc.replicated[%d].%s.%s";		
    
	@Override
	public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {

        if (!isTypeEnabled(environment, TYPE)) {
            return;
        }

        /* 
         * Group bindings together by correlation and filter out those that don't have a correlation
         * or don't have a valid function
         */
        final var bindingsCorrelations = bindings.filterBindings(TYPE)
        		.stream()
        		.filter(b -> {
        			if (!StringUtils.hasText(b.getSecret().get(CORRELATION_FIELD))) {
        			
        				LOG.warn(String.format("Replicate postgres binding %s is missing required correlation filed and will be ignored", b.getName()));
        				return false;
        			}
        			
        			final var fnc = b.getSecret().getOrDefault(FUNCTION_FILED, "").toLowerCase();
        			if (fnc.compareTo(RW_FUNCTION) != 0 && fnc.compareTo(RO_FUNCTION) != 0) {
        			
        				LOG.warn(String.format("Replicate postgres binding %s does not have a valid function and will be ignored", b.getName()));
        				return false;       				
        			}
        			return true;
        		})
        		.collect(Collectors.groupingBy(b -> b.getSecret().get(CORRELATION_FIELD)));
        
        final var cnt = new AtomicInteger(0);
        
        bindingsCorrelations.forEach((correlationName, binds) -> {
	
        	final var idx = cnt.getAndIncrement();
        	
        	binds.stream().forEach(binding -> {
        		final var fnc = binding.getSecret().get(FUNCTION_FILED).toLowerCase();
        		 
             	final var map = new MapMapper(binding.getSecret(), properties);
             	
             	map.from("correlation").to(String.format(JDBC_BASE_TEMPLATE, idx, "name"));
             	
                map.from("password").to(formatJDBCTemplate(idx, fnc, "password"));
                
                map.from("host", "port", "database").to(formatJDBCTemplate(idx, fnc, "url"),
                        (host, port, database) -> String.format("jdbc:postgresql://%s:%s/%s", host, port, database));

                String sslParam = buildSslModeParam(binding);
                String sslModeOptions = buildDbOptions(binding);
                if (!"".equals(sslParam) && !"".equals(sslModeOptions)) {
                    sslModeOptions = sslParam + "&" + sslModeOptions;
                } else if (!"".equals(sslParam) ) {
                    sslModeOptions = sslParam;
                }

                if (!"".equals(sslModeOptions)) {
                    properties.put(formatJDBCTemplate(idx, fnc, "url") , 
                    		properties.get(formatJDBCTemplate(idx, fnc, "url")) + "?" + sslModeOptions);
                }
                map.from("username").to(formatJDBCTemplate(idx, fnc, "username"));

                // jdbcURL takes precedence
                map.from("jdbc-url").to(formatJDBCTemplate(idx, fnc, "url"));

                properties.put(formatJDBCTemplate(idx, fnc, "driver-class-name"), "org.postgresql.Driver");

                //r2dbc properties
             	map.from("correlation").to(String.format(R2DBC_BASE_TEMPLATE, idx, "name"));
                map.from("password").to(formatR2DBCTemplate(idx, fnc, "password"));
                map.from("host", "port", "database").to(formatR2DBCTemplate(idx, fnc, "url"),
                        (host, port, database) -> String.format("r2dbc:postgresql://%s:%s/%s", host, port, database));
                if (!"".equals(sslModeOptions)) {
                    properties.put(formatR2DBCTemplate(idx, fnc, "url") , 
                    		properties.get(formatR2DBCTemplate(idx, fnc, "url")) + "?" + sslModeOptions);
                }
                map.from("username").to(formatR2DBCTemplate(idx, fnc, "username"));

                // r2dbcURL takes precedence
                map.from("r2dbc-url").to(formatR2DBCTemplate(idx, fnc, "url"));
             	
        	});
        });
	}	
	
	private String formatJDBCTemplate(int idx, String fnc, String field)
	{
		return String.format(JDBC_PROPERTY_TEMPLATE, idx, fnc, field);
	}
	
	private String formatR2DBCTemplate(int idx, String fnc, String field)
	{
		return String.format(R2DBC_PROPERTY_TEMPLATE, idx, fnc, field);
	}
	
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        LOG.replayTo(getClass());
    }
}
