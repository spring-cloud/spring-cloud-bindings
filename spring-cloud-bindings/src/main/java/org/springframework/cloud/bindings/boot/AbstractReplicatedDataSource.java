package org.springframework.cloud.bindings.boot;

import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

import java.util.Map;

import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.core.env.Environment;

public abstract class AbstractReplicatedDataSource implements BindingsPropertiesProcessor {

    public static final String RW_FUNCTION = "rw";
    
    public static final String RO_FUNCTION = "ro";
    
    public static final String JDBC_BASE_TEMPLATE = "spring.datasource.replicated.%s";
    
    public static final String R2DBC_BASE_TEMPLATE = "spring.r2dbc.replicated.%s";
    
    public static final String JDBC_PROPERTY_TEMPLATE = "spring.datasource.replicated.%s.%s";

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
    
	protected String formatJDBCTemplate(String fnc, String field)
	{
		return String.format(JDBC_PROPERTY_TEMPLATE, fnc, field);
	}
	
	protected String formatR2DBCTemplate(String fnc, String field)
	{
		return String.format(R2DBC_PROPERTY_TEMPLATE, fnc, field);
	}
	
	protected abstract void buildProperties(MapMapper map, Map<String, Object> properties,  Binding binding, String fnc);
	
	protected abstract String getType();
	    
}
