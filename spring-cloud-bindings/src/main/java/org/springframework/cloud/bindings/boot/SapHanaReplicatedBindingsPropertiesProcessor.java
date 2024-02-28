package org.springframework.cloud.bindings.boot;

import java.util.Map;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.cloud.bindings.Binding;
import org.springframework.context.ApplicationListener;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of type: {@value TYPE}.
 */
public class SapHanaReplicatedBindingsPropertiesProcessor extends AbstractReplicatedDataSource
   implements ApplicationListener<ApplicationPreparedEvent>  {

    /**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "hana-replicated";
    
    private static final DeferredLog LOG = new DeferredLog();	
    
    @Override
	protected void buildProperties(MapMapper map, Map<String, Object> properties,  Binding binding, String fnc)
	{
        //jdbc properties
        map.from(fnc + "-username").to(formatJDBCTemplate(fnc, "username"));
        map.from(fnc + "-password").to(formatJDBCTemplate(fnc, "password"));
        map.from(fnc + "-host", fnc + "-port", fnc + "-database").to(formatJDBCTemplate(fnc, "url"),
        		(host, port, database) -> String.format("jdbc:sap://%s:%s/%s", host, port, database));

        // jdbcURL takes precedence
        map.from(fnc + "-jdbc-url").to(formatJDBCTemplate(fnc, "url"));

        properties.put(formatJDBCTemplate(fnc, "driver-class-name"), "com.sap.db.jdbc.Driver");
        
        //r2dbc properties
        map.from(fnc + "-username").to(formatR2DBCTemplate(fnc, "username"));
        map.from(fnc + "-password").to(formatR2DBCTemplate(fnc, "password"));
        map.from(fnc + "-host", fnc + "-port", fnc + "-database").to(formatR2DBCTemplate(fnc, "url"),
                (host, port, database) -> String.format("r2dbc:sap://%s:%s/%s", host, port, database));

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
}
