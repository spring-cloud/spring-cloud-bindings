package org.springframework.cloud.bindings.boot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.PostgreSqlReplicatedBindingsPropertiesProcessor.TYPE;

import java.nio.file.Paths;
import java.util.HashMap;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.FluentMap;
import org.springframework.mock.env.MockEnvironment;

@DisplayName("PostgreSQLReplicated BindingsPropertiesProcessor")
public class PostgreSqlReplicatedBindingsPropertiesProcessorTest {

    private final FluentMap rwsecret = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("database", "testrw-database")
            .withEntry("host", "testrw-host")
            .withEntry("password", "testrw-password")
            .withEntry("port", "testrw-port")
            .withEntry("username", "testrw-username")
            .withEntry("correlation", "test-correlation")
            .withEntry("function", "rw");

    private final FluentMap rosecret = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("database", "testro-database")
            .withEntry("host", "testro-host")
            .withEntry("password", "testro-password")
            .withEntry("port", "testro-port")
            .withEntry("username", "testro-username")
            .withEntry("correlation", "test-correlation")
            .withEntry("function", "ro");
    
    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();
    
    @Test
    @DisplayName("composes jdbc url from host port and database")
    void testJdbc() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret),
                new Binding("dbro", Paths.get("dbro-path"), rosecret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated[0].rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated[0].rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated[0].rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database")
                .containsEntry("spring.datasource.replicated[0].rw.username", "testrw-username")
                .containsEntry("spring.datasource.replicated[0].ro.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated[0].ro.password", "testro-password")
                .containsEntry("spring.datasource.replicated[0].ro.url", "jdbc:postgresql://testro-host:testro-port/testro-database")
                .containsEntry("spring.datasource.replicated[0].ro.username", "testro-username")                
                .containsEntry("spring.datasource.replicated[0].name", "test-correlation");
        
    }  
    
    private final FluentMap rwsecret2 = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("database", "test2rw-database")
            .withEntry("host", "test2rw-host")
            .withEntry("password", "test2rw-password")
            .withEntry("port", "test2rw-port")
            .withEntry("username", "test2rw-username")
            .withEntry("correlation", "test2-correlation")
            .withEntry("function", "rw");

    private final FluentMap rosecret2 = new FluentMap()
            .withEntry(Binding.TYPE, TYPE)
            .withEntry("database", "test2ro-database")
            .withEntry("host", "test2ro-host")
            .withEntry("password", "test2ro-password")
            .withEntry("port", "test2ro-port")
            .withEntry("username", "test2ro-username")
            .withEntry("correlation", "test2-correlation")
            .withEntry("function", "ro");
        
    
    @Test
    @DisplayName("composes multiple replications")
    void testMultipleReplications() {
        Bindings bindings = new Bindings(
                new Binding("dbrw2", Paths.get("dbrw-path"), rwsecret2),
                new Binding("dbro2", Paths.get("dbro-path"), rosecret2),        		
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret),
                new Binding("dbro", Paths.get("dbro-path"), rosecret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
		        .containsEntry("spring.datasource.replicated[0].rw.driver-class-name", "org.postgresql.Driver")
		        .containsEntry("spring.datasource.replicated[0].rw.password", "test2rw-password")
		        .containsEntry("spring.datasource.replicated[0].rw.url", "jdbc:postgresql://test2rw-host:test2rw-port/test2rw-database")
		        .containsEntry("spring.datasource.replicated[0].rw.username", "test2rw-username")
		        .containsEntry("spring.datasource.replicated[0].ro.driver-class-name", "org.postgresql.Driver")
		        .containsEntry("spring.datasource.replicated[0].ro.password", "test2ro-password")
		        .containsEntry("spring.datasource.replicated[0].ro.url", "jdbc:postgresql://test2ro-host:test2ro-port/test2ro-database")
		        .containsEntry("spring.datasource.replicated[0].ro.username", "test2ro-username")                
		        .containsEntry("spring.datasource.replicated[0].name", "test2-correlation")
        
                .containsEntry("spring.datasource.replicated[1].rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated[1].rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated[1].rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database")
                .containsEntry("spring.datasource.replicated[1].rw.username", "testrw-username")
                .containsEntry("spring.datasource.replicated[1].ro.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated[1].ro.password", "testro-password")
                .containsEntry("spring.datasource.replicated[1].ro.url", "jdbc:postgresql://testro-host:testro-port/testro-database")
                .containsEntry("spring.datasource.replicated[1].ro.username", "testro-username")                
                .containsEntry("spring.datasource.replicated[1].name", "test-correlation");
            
        
        
    }  
    
    @Test
    @DisplayName("gives precedence to jdbc-url")
    void testJdbcURL() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret.withEntry("jdbc-url", "testrw-jdbc-url")),
                new Binding("dbro", Paths.get("dbro-path"), rosecret.withEntry("jdbc-url", "testro-jdbc-url"))      
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
		        .containsEntry("spring.datasource.replicated[0].rw.driver-class-name", "org.postgresql.Driver")
		        .containsEntry("spring.datasource.replicated[0].rw.password", "testrw-password")
		        .containsEntry("spring.datasource.replicated[0].rw.url", "testrw-jdbc-url")
		        .containsEntry("spring.datasource.replicated[0].rw.username", "testrw-username")
		        .containsEntry("spring.datasource.replicated[0].ro.driver-class-name", "org.postgresql.Driver")
		        .containsEntry("spring.datasource.replicated[0].ro.password", "testro-password")
		        .containsEntry("spring.datasource.replicated[0].ro.url", "testro-jdbc-url")
		        .containsEntry("spring.datasource.replicated[0].ro.username", "testro-username")
                .containsEntry("spring.datasource.replicated[0].name", "test-correlation");
    } 
    
    @Test
    @DisplayName("composes r2dbc url from host port and database")
    void testR2dbc() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret),
                new Binding("dbro", Paths.get("dbro-path"), rosecret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
        .containsEntry("spring.r2dbc.replicated[0].rw.password", "testrw-password")
        .containsEntry("spring.r2dbc.replicated[0].rw.url", "r2dbc:postgresql://testrw-host:testrw-port/testrw-database")
        .containsEntry("spring.r2dbc.replicated[0].rw.username", "testrw-username")
        .containsEntry("spring.r2dbc.replicated[0].ro.password", "testro-password")
        .containsEntry("spring.r2dbc.replicated[0].ro.url", "r2dbc:postgresql://testro-host:testro-port/testro-database")
        .containsEntry("spring.r2dbc.replicated[0].ro.username", "testro-username")                
        .containsEntry("spring.r2dbc.replicated[0].name", "test-correlation");
    }   
    
    @Test
    @DisplayName("gives precedence to r2dbc-url")
    void testR2dbcURL() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret.withEntry("r2dbc-url", "testrw-r2dbc-url")),
                new Binding("dbro", Paths.get("dbro-path"), rosecret.withEntry("r2dbc-url", "testro-r2dbc-url"))          		
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
        .containsEntry("spring.r2dbc.replicated[0].rw.password", "testrw-password")
        .containsEntry("spring.r2dbc.replicated[0].rw.url", "testrw-r2dbc-url")
        .containsEntry("spring.r2dbc.replicated[0].rw.username", "testrw-username")
        .containsEntry("spring.r2dbc.replicated[0].ro.password", "testro-password")
        .containsEntry("spring.r2dbc.replicated[0].ro.url", "testro-r2dbc-url")
        .containsEntry("spring.r2dbc.replicated[0].ro.username", "testro-username")
        .containsEntry("spring.r2dbc.replicated[0].name", "test-correlation");
    }    
    

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret),
                new Binding("dbro", Paths.get("dbro-path"), rosecret)
        );
        environment.setProperty("org.springframework.cloud.bindings.boot.postgresql-replicated.enable", "false");

        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }    
    
    @Test
    @DisplayName("composes jdbc url from host port and database with sslmode and crdb option")
    void testJdbcWithSsl() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret.withEntry("sslmode", "verify-full")
                		.withEntry("sslrootcert", "root.cert")
                		.withEntry("options", "--cluster=routing-id&opt=val1")),
                new Binding("dbro", Paths.get("dbro-path"), rosecret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated[0].rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated[0].rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated[0].rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database?sslmode=verify-full&sslrootcert=dbrw-path/root.cert&options=--cluster=routing-id -c opt=val1")
                .containsEntry("spring.datasource.replicated[0].rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated[0].name", "test-correlation");
    }
    
    @Test
    @DisplayName("composes jdbc url from host port and database with sslmode and crdb option")
    void testJdbcWithInvalidCrdbOption() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret.withEntry("sslmode", "verify-full")
                		.withEntry("sslrootcert", "root.cert")
                		.withEntry("options", "-cluster=routing-id&opt=val1")),
                new Binding("dbro", Paths.get("dbro-path"), rosecret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated[0].rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated[0].rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated[0].rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database?sslmode=verify-full&sslrootcert=dbrw-path/root.cert&options=-c -cluster=routing-id -c opt=val1")
                .containsEntry("spring.datasource.replicated[0].rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated[0].name", "test-correlation");
    }    
    
    @Test
    @DisplayName("composes r2dbc url from host port and database with sslmode and crdb option")
    void testR2dbcWithSsl() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret.withEntry("sslmode", "verify-full")
                		.withEntry("sslrootcert", "root.cert")
                		.withEntry("options", "--cluster=routing-id&opt=val1")),
                new Binding("dbro", Paths.get("dbro-path"), rosecret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.r2dbc.replicated[0].rw.password", "testrw-password")
                .containsEntry("spring.r2dbc.replicated[0].rw.url", "r2dbc:postgresql://testrw-host:testrw-port/testrw-database?sslmode=verify-full&sslrootcert=dbrw-path/root.cert&options=--cluster=routing-id -c opt=val1")
                .containsEntry("spring.r2dbc.replicated[0].rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated[0].name", "test-correlation");
    }   
    
    @Test
    @DisplayName("composes jdbc url from host port and database with sslmode disable")
    void testJdbcWithSslDisable() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret.withEntry("sslmode", "disable")),
                new Binding("dbro", Paths.get("dbro-path"), rosecret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated[0].rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated[0].rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated[0].rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database?sslmode=disable")
                .containsEntry("spring.datasource.replicated[0].rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated[0].name", "test-correlation");
    }    
    
    @Test
    @DisplayName("composes jdbc url from host port and database with DB options")
    void testJdbcWithDBoptions() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret.withEntry("options", "opt1=val1&opt2=val2")),
                new Binding("dbro", Paths.get("dbro-path"), rosecret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated[0].rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated[0].rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated[0].rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database?options=-c opt1=val1 -c opt2=val2")
                .containsEntry("spring.datasource.replicated[0].rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated[0].name", "test-correlation");
    }   
    
    @Test
    @DisplayName("composes jdbc url from host port and database with invalid DB options")
    void testJdbcWithInvaildDBoptions() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret.withEntry("options", "opt1=val1&opt")),
                new Binding("dbro", Paths.get("dbro-path"), rosecret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated[0].rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated[0].rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated[0].rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database?options=-c opt1=val1")
                .containsEntry("spring.datasource.replicated[0].rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated[0].name", "test-correlation");
    }   
    
    @Test
    @DisplayName("composes jdbc url from host port and database with empty DB options")
    void testJdbcWithEmptyDBoptions() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret.withEntry("options", "")),
                new Binding("dbro", Paths.get("dbro-path"), rosecret)
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.datasource.replicated[0].rw.driver-class-name", "org.postgresql.Driver")
                .containsEntry("spring.datasource.replicated[0].rw.password", "testrw-password")
                .containsEntry("spring.datasource.replicated[0].rw.url", "jdbc:postgresql://testrw-host:testrw-port/testrw-database")
                .containsEntry("spring.datasource.replicated[0].rw.username", "testrw-username")
                .containsEntry("spring.r2dbc.replicated[0].name", "test-correlation");
    }
    
    @Test
    @DisplayName("composes invalid replication due to no correlation")
    void testJdbcNoCorrection() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret.withEntry("correlation", "")),
                new Binding("dbro", Paths.get("dbro-path"), rosecret.withEntry("correlation", ""))
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties).isEmpty();
    }
    
    @Test
    @DisplayName("composes invalid replication due to invalide fucntion")
    void testJdbcInvalidFunction() {
        Bindings bindings = new Bindings(
                new Binding("dbrw", Paths.get("dbrw-path"), rwsecret.withEntry("function", "invalid")),
                new Binding("dbro", Paths.get("dbro-path"), rosecret.withEntry("function", "invalid"))
        );
        new PostgreSqlReplicatedBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties).isEmpty();
    }    
}
