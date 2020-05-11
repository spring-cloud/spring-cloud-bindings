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
Each auto-configuration is triggered by the kind of binding.  Each auto-configuration can be disabled using a System Property specific to that kind and defaults to enable. Auto-configuration is disabled by default and can be enabled by setting the `org.springframework.cloud.bindings.boot.enable` System Property to `true`.

### Cassandra
Kind: `cassandra`
Disable Property: `org.springframework.cloud.bindings.boot.cassandra.enable`

| Property | Value (`{secret}`)
| -------- | ------------------
| `spring.data.cassandra.cluster-name` | `{cluster-name}`
| `spring.data.cassandra.compression` | `{compression}`
| `spring.data.cassandra.contact-points` | `{contact-points}`
| `spring.data.cassandra.keyspace-name` | `{keyspace-name}`
| `spring.data.cassandra.password` | `{password}`
| `spring.data.cassandra.port` | `{port}`
| `spring.data.cassandra.ssl` | `{ssl}`
| `spring.data.cassandra.username` | `{username}`

### Couchbase
Kind: `couchbase`
Disable Property: `org.springframework.cloud.bindings.boot.couchbase.enable`

| Property | Value (`{secret}`)
| -------- | ------------------
| `spring.couchbase.bootstrap-hosts` | `{bootstrap-hosts}`
| `spring.couchbase.bucket.name` | `{bucket.name}`
| `spring.couchbase.bucket.password` | `{bucket.passsword}`
| `spring.couchbase.env.bootstrap.http-direct-port` | `{env.bootstrap.http-direct-port}`
| `spring.couchbase.env.bootstrap.http-ssl-port` | `{env.bootstrap.http-ssl-port}`
| `spring.couchbase.password` | `{password}`
| `spring.couchbase.username` | `{username}`

### DB2 RDBMS
Kind: `DB2`
Disable Property: `org.springframework.cloud.bindings.boot.db2.enable`

| Property | Value (`{secret}`)
| -------- | ------------------
| `spring.datasource.driver-class-name` | `com.ibm.db2.jcc.DB2Driver`
| `spring.datasource.password` | `{password}`
| `spring.datasource.url` | `jdbc:db2://{host}:{port}/{database}`
| `spring.datasource.username` | `{username}`


### Elasticsearch
Kind: `Elasticsearch`
Disable Property: `org.springframework.cloud.bindings.boot.elasticsearch.enable`

| Property | Value (`{secret}`)
| -------- | ------------------
| `spring.data.elasticsearch.client.reactive.endpoints` | `{endpoints}`
| `spring.data.elasticsearch.client.reactive.password` | `{password}`
| `spring.data.elasticsearch.client.reactive.use-ssl` | `{use-ssl}`
| `spring.data.elasticsearch.client.reactive.username` | `{username}`
| `spring.elasticsearch.jest.password` | `{password}`
| `spring.elasticsearch.jest.proxy.host` | `{proxy.host}`
| `spring.elasticsearch.jest.proxy.port` | `{proxy.port}`
| `spring.elasticsearch.jest.username` | `{username}`
| `spring.elasticsearch.rest.password` | `{password}`
| `spring.elasticsearch.rest.uris` | `{uris}`
| `spring.elasticsearch.rest.username` | `{username}`

### MongoDB
Kind: `MongoDB`
Disable Property: `org.springframework.cloud.bindings.boot.mongodb.enable`

| Property | Value (`{secret}`)
| -------- | ------------------
| `spring.mongodb.authentication-database` | `{authentication-database}`
| `spring.mongodb.database` | `{database}`
| `spring.mongodb.grid-fs-database` | `{grid-fs-database}`
| `spring.mongodb.host` | `{host}`
| `spring.mongodb.password` | `{password}`
| `spring.mongodb.port` | `{port}`
| `spring.mongodb.uri` | `{uri}`
| `spring.mongodb.username` | `{username}`

### MySQL RDBMS
Kind: `MySQL`
Disable Property: `org.springframework.cloud.bindings.boot.mysql.enable`

| Property | Value (`{secret}`)
| -------- | ------------------
| `spring.datasource.driver-class-name` | `org.mariadb.jdbc.Driver` or `com.mysql.cj.jdbc.Driver` depending on classpath
| `spring.datasource.password` | `{password}`
| `spring.datasource.url` | `jdbc:mysql://{host}:{port}/{database}`
| `spring.datasource.username` | `{username}`

### Oracle RDBMS
Kind: `Oracle`
Disable Property: `org.springframework.cloud.bindings.boot.oracle.enable`

| Property | Value (`{secret}`)
| -------- | ------------------
| `spring.datasource.driver-class-name` | `oracle.jdbc.OracleDriver`
| `spring.datasource.password` | `{password}`
| `spring.datasource.url` | `jdbc:oracle://{host}:{port}/{database}`
| `spring.datasource.username` | `{username}`

### PostgreSQL RDBMS
Kind: `PostgreSQL`
Disable Property: `org.springframework.cloud.bindings.boot.postgresql.enable`

| Property | Value (`{secret}`)
| -------- | ------------------
| `spring.datasource.driver-class-name` | `org.postgresql.Driver`
| `spring.datasource.password` | `{password}`
| `spring.datasource.url` | `jdbc:postgres://{host}:{port}/{database}`
| `spring.datasource.username` | `{username}`

### Redis RDBMS
Kind: `Redis`
Disable Property: `org.springframework.cloud.bindings.boot.redis.enable`

| Property | Value (`{secret}`)
| -------- | ------------------
| `spring.redis.client-name` | `{client-name}`
| `spring.redis.cluster.max-redirects` | `{cluster.max-redirects}`
| `spring.redis.cluster.nodes` | `{cluster-nodes}`
| `spring.redis.database` | `{database}`
| `spring.redis.host` | `{host}`
| `spring.redis.password` | `{password}`
| `spring.redis.port` | `{port}`
| `spring.redis.sentinel.master` | `{sentinel.master}`
| `spring.redis.sentinel.nodes` | `{sentinel.nodes}`
| `spring.redis.ssl` | `{ssl}`
| `spring.redis.url` | `{url}`

### SQLServer RDBMS
Kind: `SQLServer`
Disable Property: `org.springframework.cloud.bindings.boot.sqlserver.enable`

| Property | Value (`{secret}`)
| -------- | ------------------
| `spring.datasource.driver-class-name` | `com.microsoft.sqlserver.jdbc.SQLServerDriver`
| `spring.datasource.password` | `{password}`
| `spring.datasource.url` | `jdbc:sqlserver://{host}:{port}/{database}`
| `spring.datasource.username` | `{username}`

## License
This buildpack is released under version 2.0 of the [Apache License][a].

[a]: http://www.apache.org/licenses/LICENSE-2.0
