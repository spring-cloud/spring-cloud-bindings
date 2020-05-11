# Spring Cloud Bindings
The Spring Cloud Bindings library exposes a rich Java language binding for the [Cloud Native Buildpacks Binding Specification][s].  In addition, if opted-in, it configures Spring Boot application configuration properties appropriate for the kind of binding encountered.

[s]: https://github.com/buildpacks/spec/blob/master/extensions/bindings.md

## Library Usage
While less used, explicit binding access can be achieved through the `Bindings` type.


```
List<Binding> bindings = new Bindings().filterBindings("MySQL");
if (bindings.size() > 0) {
    Map<String, String> secret = bindings.get(0).getSecret();

    MariaDbDataSource dataSource = new MariaDbDataSource();
    dataSource.setServerName(secret.get("host"));
    dataSource.setPort(Integer.parseInt(secret.get("port")));
    dataSource.setDatabaseName(secret.get("database"));
    dataSource.setUserName(secret.get("username"));
    dataSource.setPassword(secret.get("password"));

    return dataSource;
}
```

## Spring Boot Configuration
The more common usage of the library is opt-in automatic Spring Boot configuration.  Setting the `org.springframework.cloud.bindings.boot.enabled=true` System Property results in the following:

* Adds a `PropertySource` with a flattened representation (`cnb.bindings.{name}.{metadata,secret}.*`) of the bindings.
* Adds a `PropertySource` with binding-specific Spring Boot configuration properties.

## Auto-Configurations

### Cassandra
Kind: `cassandra`
Property: `org.springframework.cloud.bindings.boot.cassandra.enabled`

| Property | Value
| -------- | -----
| `spring.data.cassandra.contact-points` | `{secret.node_ips}`
| `spring.data.cassandra.password` | `{secret.password}`
| `spring.data.cassandra.port` | `{secret.port}`
| `spring.data.cassandra.username` | `{secret.username}`

### DB2 RDBMS
Kind: `DB2`
Property: `org.springframework.cloud.bindings.boot.db2.enabled`

| Property | Value
| -------- | -----
| `spring.datasource.driver-class-name` | `com.ibm.db2.jcc.DB2Driver`
| `spring.datasource.password` | `{secret.password}`
| `spring.datasource.url` | `jdbc:db2://{secret.host}:{secret.port}/{secret.database}`
| `spring.datasource.username` | `{secret.username}`

### MongoDB
Kind: `MongoDB`
Property: `org.springframework.cloud.bindings.boot.mongodb.enabled`

| Property | Value
| -------- | -----
| `spring.mongodb.uri` | `{secret.uri}`

### MySQL RDBMS
Kind: `MySQL`
Property: `org.springframework.cloud.bindings.boot.mysql.enabled`

| Property | Value
| -------- | -----
| `spring.datasource.driver-class-name` | `org.mariadb.jdbc.Driver` or `com.mysql.cj.jdbc.Driver` depending on classpath
| `spring.datasource.password` | `{secret.password}`
| `spring.datasource.url` | `jdbc:mysql://{secret.host}:{secret.port}/{secret.database}`
| `spring.datasource.username` | `{secret.username}`

### Oracle RDBMS
Kind: `Oracle`
Property: `org.springframework.cloud.bindings.boot.oracle.enabled`

| Property | Value
| -------- | -----
| `spring.datasource.driver-class-name` | `oracle.jdbc.OracleDriver`
| `spring.datasource.password` | `{secret.password}`
| `spring.datasource.url` | `jdbc:oracle://{secret.host}:{secret.port}/{secret.database}`
| `spring.datasource.username` | `{secret.username}`

### PostgreSQL RDBMS
Kind: `PostgreSQL`
Property: `org.springframework.cloud.bindings.boot.postgresql.enabled`

| Property | Value
| -------- | -----
| `spring.datasource.driver-class-name` | `org.postgresql.Driver`
| `spring.datasource.password` | `{secret.password}`
| `spring.datasource.url` | `jdbc:postgres://{secret.host}:{secret.port}/{secret.database}`
| `spring.datasource.username` | `{secret.username}`

### Redis RDBMS
Kind: `Redis`
Property: `org.springframework.cloud.bindings.boot.redis.enabled`

| Property | Value
| -------- | -----
| `spring.datasource.host` | `{secret.host}`
| `spring.datasource.password` | `{secret.password}`
| `spring.datasource.port` | `{secret.port}`

### Oracle RDBMS
Kind: `SQLServer`
Property: `org.springframework.cloud.bindings.boot.sqlserver.enabled`

| Property | Value
| -------- | -----
| `spring.datasource.driver-class-name` | `com.microsoft.sqlserver.jdbc.SQLServerDriver`
| `spring.datasource.password` | `{secret.password}`
| `spring.datasource.url` | `jdbc:sqlserver://{secret.host}:{secret.port}/{secret.database}`
| `spring.datasource.username` | `{secret.username}`

## License
This buildpack is released under version 2.0 of the [Apache License][a].

[a]: http://www.apache.org/licenses/LICENSE-2.0
