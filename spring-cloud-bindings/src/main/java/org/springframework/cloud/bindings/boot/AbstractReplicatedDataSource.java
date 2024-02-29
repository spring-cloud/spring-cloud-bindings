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

import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

import java.util.Map;

import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.core.env.Environment;

/**
 *  Abstract class for replicated datasources processors that implement a common properties pattern.
 */
public abstract class AbstractReplicatedDataSource implements BindingsPropertiesProcessor {

    /**
     * Read/write function field
     */
    public static final String RW_FUNCTION = "rw";
    
    /**
     * Read only function field
     */
    public static final String RO_FUNCTION = "ro";
    
    /**
     * Template for a base JDBC properties
     */
    public static final String JDBC_BASE_TEMPLATE = "spring.datasource.replicated.%s";
    
    /**
     * Template for a base R2DBC properties
     */
    public static final String R2DBC_BASE_TEMPLATE = "spring.r2dbc.replicated.%s";
    
    /**
     * Template for function based JDBC properties
     */    
    public static final String JDBC_PROPERTY_TEMPLATE = "spring.datasource.replicated.%s.%s";

    /**
     * Template for function based R2DBC properties
     */     
    public static final String R2DBC_PROPERTY_TEMPLATE = "spring.r2dbc.replicated.%s.%s";	
    
    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isTypeEnabled(environment, getType())) {
            return;
        }

        bindings.filterBindings(getType()).forEach(binding -> {
         	
         	properties.put(String.format(JDBC_BASE_TEMPLATE, "name"), binding.getName());

         	properties.put(String.format(R2DBC_BASE_TEMPLATE, "name"), binding.getName());
         	
         	final var map = new MapMapper(binding.getSecret(), properties);
         	
         	buildProperties(map, properties, binding, RW_FUNCTION);
         	
         	buildProperties(map, properties, binding, RO_FUNCTION);
             	
        });
    }
    
    /**
     * Format a JDBC property
     */
	protected String formatJDBCTemplate(String fnc, String field)
	{
		return String.format(JDBC_PROPERTY_TEMPLATE, fnc, field);
	}
	
    /**
     * Format an R2DBC property
     */
	protected String formatR2DBCTemplate(String fnc, String field)
	{
		return String.format(R2DBC_PROPERTY_TEMPLATE, fnc, field);
	}
	
	/**
	 * Builds the properties for a given operations function
	 * @param map The properties mapper
	 * @param properties Properties resource where new spring properties will be written to
	 * @param binding The current binding object
	 * @param fnc The operations function of the current binding
	 */
	protected abstract void buildProperties(MapMapper map, Map<String, Object> properties,  Binding binding, String fnc);
	
	/**
	 * Gets the binding type of this processor.
	 * @return The binding type.
	 */
	protected abstract String getType();
	    
}
