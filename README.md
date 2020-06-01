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
The more common usage of the library is opt-in automatic Spring Boot configuration.  Setting the `org.springframework.cloud.bindings.boot.enable=true` System Property results in the following:

* Adds a `PropertySource` with a flattened representation (`cnb.bindings.{name}.{metadata,secret}.*`) of the bindings.
* Adds a `PropertySource` with binding-specific Spring Boot configuration properties.

## Auto-Configurations
Each auto-configuration is triggered by the kind of binding.  Each auto-configuration can be disabled using a System Property specific to that kind and defaults to enable. Auto-configuration is disabled by default and can be enabled by setting the `org.springframework.cloud.bindings.boot.enable` System Property to `true`.

### Cassandra
Kind: `cassandra`
Disable Property: `org.springframework.cloud.bindings.boot.cassandra.enable`

| Property | Value
| -------- | ------------------
| `spring.data.cassandra.cluster-name` | `{secret/cluster-name}`
| `spring.data.cassandra.compression` | `{secret/compression}`
| `spring.data.cassandra.contact-points` | `{secret/contact-points}`
| `spring.data.cassandra.keyspace-name` | `{secret/keyspace-name}`
| `spring.data.cassandra.password` | `{secret/password}`
| `spring.data.cassandra.port` | `{secret/port}`
| `spring.data.cassandra.ssl` | `{secret/ssl}`
| `spring.data.cassandra.username` | `{secret/username}`

### Couchbase
Kind: `couchbase`
Disable Property: `org.springframework.cloud.bindings.boot.couchbase.enable`

| Property | Value
| -------- | ------------------
| `spring.couchbase.bootstrap-hosts` | `{secret/bootstrap-hosts}`
| `spring.couchbase.bucket.name` | `{secret/bucket.name}`
| `spring.couchbase.bucket.password` | `{secret/bucket.passsword}`
| `spring.couchbase.env.bootstrap.http-direct-port` | `{secret/env.bootstrap.http-direct-port}`
| `spring.couchbase.env.bootstrap.http-ssl-port` | `{secret/env.bootstrap.http-ssl-port}`
| `spring.couchbase.password` | `{secret/password}`
| `spring.couchbase.username` | `{secret/username}`

### DB2 RDBMS
Kind: `DB2`
Disable Property: `org.springframework.cloud.bindings.boot.db2.enable`

| Property | Value
| -------- | ------------------
| `spring.datasource.driver-class-name` | `com.ibm.db2.jcc.DB2Driver`
| `spring.datasource.password` | `{secret/password}`
| `spring.datasource.url` | `jdbc:db2://{secret/host}:{secret/port}/{secret/database}`
| `spring.datasource.username` | `{secret/username}`
| `spring.r2dbc.url` | `r2dbc:db2://{secret/host}:{secret/port}/{secret/database}`
| `spring.r2dbc.password` | `{secret/password}`
| `spring.r2dbc.username` | `{secret/username}`


### Elasticsearch
Kind: `Elasticsearch`
Disable Property: `org.springframework.cloud.bindings.boot.elasticsearch.enable`

| Property | Value
| -------- | ------------------
| `spring.data.elasticsearch.client.reactive.endpoints` | `{secret/endpoints}`
| `spring.data.elasticsearch.client.reactive.password` | `{secret/password}`
| `spring.data.elasticsearch.client.reactive.use-ssl` | `{secret/use-ssl}`
| `spring.data.elasticsearch.client.reactive.username` | `{secret/username}`
| `spring.elasticsearch.jest.password` | `{secret/password}`
| `spring.elasticsearch.jest.proxy.host` | `{secret/proxy.host}`
| `spring.elasticsearch.jest.proxy.port` | `{secret/proxy.port}`
| `spring.elasticsearch.jest.username` | `{secret/username}`
| `spring.elasticsearch.rest.password` | `{secret/password}`
| `spring.elasticsearch.rest.uris` | `{secret/uris}`
| `spring.elasticsearch.rest.username` | `{secret/username}`

### Kafka
Kind: `kafka`
Disable Property: `org.springframework.cloud.bindings.boot.kafka.enable`

| Property | Value
| -------- | ------------------
| `spring.kafka.bootstrap-servers` | `{secret/bootstrap-servers}`
| `spring.kafka.consumer.bootstrap-servers` | `{secret/consumer.bootstrap-servers}`
| `spring.kafka.producer.bootstrap-servers` | `{secret/producer.bootstrap-servers}`
| `spring.kafka.streams.bootstrap-servers` | `{secret/streams.bootstrap-servers}`

### MongoDB
Kind: `MongoDB`
Disable Property: `org.springframework.cloud.bindings.boot.mongodb.enable`

| Property | Value
| -------- | ------------------
| `spring.mongodb.authentication-database` | `{secret/authentication-database}`
| `spring.mongodb.database` | `{secret/database}`
| `spring.mongodb.grid-fs-database` | `{secret/grid-fs-database}`
| `spring.mongodb.host` | `{secret/host}`
| `spring.mongodb.password` | `{secret/password}`
| `spring.mongodb.port` | `{secret/port}`
| `spring.mongodb.uri` | `{secret/uri}`
| `spring.mongodb.username` | `{secret/username}`

### MySQL RDBMS
Kind: `MySQL`
Disable Property: `org.springframework.cloud.bindings.boot.mysql.enable`

| Property | Value
| -------- | ------------------
| `spring.datasource.driver-class-name` | `org.mariadb.jdbc.Driver` or `com.mysql.cj.jdbc.Driver` depending on classpath
| `spring.datasource.password` | `{secret/password}`
| `spring.datasource.url` | `jdbc:mysql://{secret/host}:{secret/port}/{secret/database}`
| `spring.datasource.username` | `{secret/username}`
| `spring.r2dbc.url` | `r2dbc:mysql://{secret/host}:{secret/port}/{secret/database}`
| `spring.r2dbc.password` | `{secret/password}`
| `spring.r2dbc.username` | `{secret/username}`

### Neo4J
Kind: `Neo4J`
Disable Property: `org.springframework.cloud.bindings.boot.neo4j.enable`

| Property | Value
| -------- | ------------------
| `spring.data.neo4j.password` | `{secret/password}`
| `spring.data.neo4j.uri` | `{secret/uri}`
| `spring.data.neo4j.username` | `{secret/username}`

### Oracle RDBMS
Kind: `Oracle`
Disable Property: `org.springframework.cloud.bindings.boot.oracle.enable`

| Property | Value
| -------- | ------------------
| `spring.datasource.driver-class-name` | `oracle.jdbc.OracleDriver`
| `spring.datasource.password` | `{secret/password}`
| `spring.datasource.url` | `jdbc:oracle://{secret/host}:{secret/port}/{secret/database}`
| `spring.datasource.username` | `{secret/username}`
| `spring.r2dbc.url` | `r2dbc:oracle://{secret/host}:{secret/port}/{secret/database}`
| `spring.r2dbc.password` | `{secret/password}`
| `spring.r2dbc.username` | `{secret/username}`

### PostgreSQL RDBMS
Kind: `PostgreSQL`
Disable Property: `org.springframework.cloud.bindings.boot.postgresql.enable`

| Property | Value
| -------- | ------------------
| `spring.datasource.driver-class-name` | `org.postgresql.Driver`
| `spring.datasource.password` | `{secret/password}`
| `spring.datasource.url` | `jdbc:postgres://{secret/host}:{secret/port}/{secret/database}`
| `spring.datasource.username` | `{secret/username}`
| `spring.r2dbc.url` | `r2dbc:postgres://{secret/host}:{secret/port}/{secret/database}`
| `spring.r2dbc.password` | `{secret/password}`
| `spring.r2dbc.username` | `{secret/username}`

### RabbitMQ RDBMS
Kind: `RabbitMQ`
Disable Property: `org.springframework.cloud.bindings.boot.rabbitmq.enable`

| Property | Value
| -------- | ------------------
| `spring.rabbitmq.addresses` | `{secret/addresses}`
| `spring.rabbitmq.host` | `{secret/host}`
| `spring.rabbitmq.password` | `{secret/password}`
| `spring.rabbitmq.port` | `{secret/port}`
| `spring.rabbitmq.username` | `{secret/username}`
| `spring.rabbitmq.virtual-host` | `{secret/virtual-host}`

### Redis RDBMS
Kind: `Redis`
Disable Property: `org.springframework.cloud.bindings.boot.redis.enable`

| Property | Value
| -------- | ------------------
| `spring.redis.client-name` | `{secret/client-name}`
| `spring.redis.cluster.max-redirects` | `{secret/cluster.max-redirects}`
| `spring.redis.cluster.nodes` | `{secret/cluster-nodes}`
| `spring.redis.database` | `{secret/database}`
| `spring.redis.host` | `{secret/host}`
| `spring.redis.password` | `{secret/password}`
| `spring.redis.port` | `{secret/port}`
| `spring.redis.sentinel.master` | `{secret/sentinel.master}`
| `spring.redis.sentinel.nodes` | `{secret/sentinel.nodes}`
| `spring.redis.ssl` | `{secret/ssl}`
| `spring.redis.url` | `{secret/url}`

## SCS Config Server
Kind: `Config`
Disable Property: `org.springframework.cloud.bindings.boot.config.enable`

| Property | Value
| -------- | ------------------
| `spring.cloud.config.uri` | `{secret/uri}`
| `spring.cloud.config.client.oauth2.clientId` | `{secret/client-id}`
| `spring.cloud.config.client.oauth2.clientSecret` |  `{secret/client-secret}`
| `spring.cloud.config.client.oauth2.accessTokenUri` | `{secret/access-token-uri}`

### SQLServer RDBMS
Kind: `SQLServer`
Disable Property: `org.springframework.cloud.bindings.boot.sqlserver.enable`

| Property | Value
| -------- | ------------------
| `spring.datasource.driver-class-name` | `com.microsoft.sqlserver.jdbc.SQLServerDriver`
| `spring.datasource.password` | `{secret/password}`
| `spring.datasource.url` | `jdbc:sqlserver://{secret/host}:{secret/port}/{secret/database}`
| `spring.datasource.username` | `{secret/username}`
| `spring.r2dbc.url` | `r2dbc:sqlserver://{secret/host}:{secret/port}/{secret/database}`
| `spring.r2dbc.password` | `{secret/password}`
| `spring.r2dbc.username` | `{secret/username}`

### Wavefront

Kind: `Wavefront`
Disable Property: `org.springframework.cloud.bindings.boot.wavefront.enable`

| Property | Value
| -------- | ------------------
| `management.metrics.export.wavefront.api-token` | `{secret/api-token}`
| `management.metrics.export.wavefront.uri` | `{secret/uri}`

### Eureka
Kind: `Eureka`
Disable Property: `org.springframework.cloud.bindings.boot.eureka.enable`

| Property | Value
| -------- | ------------------
| `eureka.client.oauth2.client-id` | `{secret/client-id}`
| `eureka.client.oauth2.access-token-uri` | `{secret/access-token-uri}`
| `eureka.client.region` | `default`
| `eureka.client.serviceUrl.defaultZone` | `{secret/uri}/eureka/`

## License
This buildpack is released under version 2.0 of the [Apache License][a].

[a]: http://www.apache.org/licenses/LICENSE-2.0
