# Spring Cloud Bindings
The Spring Cloud Bindings library exposes a rich Java language binding for the [Cloud Native Buildpacks Binding Specification][s].  In addition, if opted-in, it configures Spring Boot application configuration properties appropriate for the type of binding encountered.

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

* Adds a `PropertySource` with a flattened representation (`k8s.bindings.{name}.*`) of the bindings.
* Adds a `PropertySource` with binding-specific Spring Boot configuration properties.

## Auto-Configurations
Each auto-configuration is triggered by the type of binding.  Each auto-configuration can be disabled using a System Property specific to that type and defaults to enable. Auto-configuration is disabled by default and can be enabled by setting the `org.springframework.cloud.bindings.boot.enable` System Property to `true`.

`{<key>}` indicates that the value is the contents of the secret with the given key.

### Cassandra
Type: `cassandra`
Disable Property: `org.springframework.cloud.bindings.boot.cassandra.enable`

| Property | Value
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
Type: `couchbase`
Disable Property: `org.springframework.cloud.bindings.boot.couchbase.enable`

| Property | Value
| -------- | ------------------
| `spring.couchbase.bootstrap-hosts` | `{bootstrap-hosts}`
| `spring.couchbase.bucket.name` | `{bucket.name}`
| `spring.couchbase.bucket.password` | `{bucket.passsword}`
| `spring.couchbase.env.bootstrap.http-direct-port` | `{env.bootstrap.http-direct-port}`
| `spring.couchbase.env.bootstrap.http-ssl-port` | `{env.bootstrap.http-ssl-port}`
| `spring.couchbase.password` | `{password}`
| `spring.couchbase.username` | `{username}`

### DB2 RDBMS
Type: `db2`
Disable Property: `org.springframework.cloud.bindings.boot.db2.enable`

| Property | Value
| -------- | ------------------
| `spring.datasource.driver-class-name` | `com.ibm.db2.jcc.DB2Driver`
| `spring.datasource.password` | `{password}`
| `spring.datasource.url` | `{jdbc-url}` or `jdbc:db2://{host}:{port}/{database}`
| `spring.datasource.username` | `{username}`
| `spring.r2dbc.url` | `{r2dbc-url}` or `r2dbc:db2://{host}:{port}/{database}`
| `spring.r2dbc.password` | `{password}`
| `spring.r2dbc.username` | `{username}`


### Elasticsearch
Type: `elasticsearch`
Disable Property: `org.springframework.cloud.bindings.boot.elasticsearch.enable`

| Property | Value
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

### Kafka
Type: `kafka`
Disable Property: `org.springframework.cloud.bindings.boot.kafka.enable`

| Property | Value
| -------- | ------------------
| `spring.kafka.bootstrap-servers` | `{bootstrap-servers}`
| `spring.kafka.consumer.bootstrap-servers` | `{consumer.bootstrap-servers}`
| `spring.kafka.producer.bootstrap-servers` | `{producer.bootstrap-servers}`
| `spring.kafka.streams.bootstrap-servers` | `{streams.bootstrap-servers}`

### LDAP
Type: `ldap`
Disable Property: `org.springframework.cloud.bindings.boot.ldap.enable`

| Property | Value
| -------- | ------------------
| `spring.ldap.base` | `{base}`
| `spring.ldap.password` | `{password}`
| `spring.ldap.urls` | `{urls}`
| `spring.ldap.username` | `{username}`

### MongoDB
Type: `mongodb`
Disable Property: `org.springframework.cloud.bindings.boot.mongodb.enable`

| Property | Value
| -------- | ------------------
| `spring.data.mongodb.authentication-database` | `{authentication-database}`
| `spring.data.mongodb.database` | `{database}`
| `spring.data.mongodb.gridfs.database"` | `{grid-fs-database}`
| `spring.data.mongodb.host` | `{host}`
| `spring.data.mongodb.password` | `{password}`
| `spring.data.mongodb.port` | `{port}`
| `spring.data.mongodb.uri` | `{uri}`
| `spring.data.mongodb.username` | `{username}`

### MySQL RDBMS
Type: `mysql`
Disable Property: `org.springframework.cloud.bindings.boot.mysql.enable`

| Property | Value
| -------- | ------------------
| `spring.datasource.driver-class-name` | `org.mariadb.jdbc.Driver` or `com.mysql.cj.jdbc.Driver` depending on classpath
| `spring.datasource.password` | `{password}`
| `spring.datasource.url` | `{jdbc-url}` or `jdbc:mysql://{host}:{port}/{database}`
| `spring.datasource.username` | `{username}`
| `spring.r2dbc.url` | `{r2dbc-url}` or `r2dbc:mysql://{host}:{port}/{database}`
| `spring.r2dbc.password` | `{password}`
| `spring.r2dbc.username` | `{username}`

### Neo4J
Type: `neo4j`
Disable Property: `org.springframework.cloud.bindings.boot.neo4j.enable`

| Property | Value
| -------- | ------------------
| `spring.data.neo4j.password` | `{password}`
| `spring.data.neo4j.uri` | `{uri}`
| `spring.data.neo4j.username` | `{username}`

### Oracle RDBMS
Type: `oracle`
Disable Property: `org.springframework.cloud.bindings.boot.oracle.enable`

| Property | Value
| -------- | ------------------
| `spring.datasource.driver-class-name` | `oracle.jdbc.OracleDriver`
| `spring.datasource.password` | `{password}`
| `spring.datasource.url` | `{jdbc-url}` or `jdbc:oracle://{host}:{port}/{database}`
| `spring.datasource.username` | `{username}`
| `spring.r2dbc.url` | `{r2dbc-url}` or `r2dbc:oracle://{host}:{port}/{database}`
| `spring.r2dbc.password` | `{password}`
| `spring.r2dbc.username` | `{username}`

### PostgreSQL RDBMS
Type: `postgresql`
Disable Property: `org.springframework.cloud.bindings.boot.postgresql.enable`

| Property | Value
| -------- | ------------------
| `spring.datasource.driver-class-name` | `org.postgresql.Driver`
| `spring.datasource.password` | `{password}`
| `spring.datasource.url` | `{jdbc-url}` or `jdbc:postgres://{host}:{port}/{database}`
| `spring.datasource.username` | `{username}`
| `spring.r2dbc.url` | `{r2dbc-url}` or `r2dbc:postgres://{host}:{port}/{database}`
| `spring.r2dbc.password` | `{password}`
| `spring.r2dbc.username` | `{username}`

### RabbitMQ
Type: `rabbitmq`
Disable Property: `org.springframework.cloud.bindings.boot.rabbitmq.enable`

| Property | Value
| -------- | ------------------
| `spring.rabbitmq.addresses` | `{addresses}`
| `spring.rabbitmq.host` | `{host}`
| `spring.rabbitmq.password` | `{password}`
| `spring.rabbitmq.port` | `{port}`
| `spring.rabbitmq.username` | `{username}`
| `spring.rabbitmq.virtual-host` | `{virtual-host}`

### Redis
Type: `redis`
Disable Property: `org.springframework.cloud.bindings.boot.redis.enable`

| Property | Value
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

## SCS Config Server
Type: `config`
Disable Property: `org.springframework.cloud.bindings.boot.config.enable`

| Property | Value
| -------- | ------------------
| `spring.cloud.config.uri` | `{uri}`
| `spring.cloud.config.client.oauth2.clientId` | `{client-id}`
| `spring.cloud.config.client.oauth2.clientSecret` |  `{client-secret}`
| `spring.cloud.config.client.oauth2.accessTokenUri` | `{access-token-uri}`

### SCS Eureka
Type: `eureka`
Disable Property: `org.springframework.cloud.bindings.boot.eureka.enable`

| Property | Value
| -------- | ------------------
| `eureka.client.oauth2.client-id` | `{client-id}`
| `eureka.client.oauth2.access-token-uri` | `{access-token-uri}`
| `eureka.client.region` | `default`
| `eureka.client.serviceUrl.defaultZone` | `{uri}/eureka/`

## Spring Security OAuth2
Type: `oauth2`
Disable Property: `org.springframework.cloud.bindings.boot.oauth2.enable`

| Property | Value
| -------- | ------------------
| `spring.security.oauth2.client.registration.{name}.client-id` | `{client-id}`
| `spring.security.oauth2.client.registration.{name}.client-secret` | `{client-secret}`
| `spring.security.oauth2.client.registration.{name}.provider` | `{provider}`
| `spring.security.oauth2.client.provider.{provider}.issuer-uri` | `{issuer-uri}`
| `spring.security.oauth2.client.provider.{provider}.authorization-uri` | `{authorization-uri}`
| `spring.security.oauth2.client.provider.{provider}.token-uri` | `{token-uri}`
| `spring.security.oauth2.client.provider.{provider}.user-info-uri` | `{user-info-uri}`
| `spring.security.oauth2.client.provider.{provider}.user-info-authentication-method` | `{user-info-authentication-method}`
| `spring.security.oauth2.client.provider.{provider}.jwk-set-uri` | `{jwk-set-uri}`
| `spring.security.oauth2.client.provider.{provider}.user-name-attribute` | `{user-name-attribute}`

### SQLServer RDBMS
Type: `sqlserver`
Disable Property: `org.springframework.cloud.bindings.boot.sqlserver.enable`

| Property | Value
| -------- | ------------------
| `spring.datasource.driver-class-name` | `com.microsoft.sqlserver.jdbc.SQLServerDriver`
| `spring.datasource.password` | `{password}`
| `spring.datasource.url` | `jdbc:sqlserver://{host}:{port}/{database}`
| `spring.datasource.username` | `{username}`
| `spring.r2dbc.url` | `{r2dbc-url}` or `r2dbc:sqlserver://{host}:{port}/{database}`
| `spring.r2dbc.password` | `{password}`
| `spring.r2dbc.username` | `{username}`


### Vault
Type: `vault`
Disable Property: `org.springframework.cloud.bindings.boot.vault.enable`

Any Provider:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.authentication` | `{authentication-method}`
| `spring.cloud.vault.namespace` | `{namespace}`
| `spring.cloud.vault.uri` | `{uri}`

If `{authentication-method}` is equal to `approle`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.app-role.app-role-path` | `{app-role-path}`
| `spring.cloud.vault.app-role.role-id` | `{role-id}`
| `spring.cloud.vault.app-role.role` | `{role}`
| `spring.cloud.vault.app-role.secret-id` | `{secret-id}`

If `{authentication-method}` is equal to `aws_ec2`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.aws-ec2.aws-ec2-path` | `{aws-ec2-path}`
| `spring.cloud.vault.aws-ec2.identity-document` | `{aws-ec2-instance-identity-document}`
| `spring.cloud.vault.aws-ec2.nonce` | `{nonce}`
| `spring.cloud.vault.aws-ec2.role` | `{role}`

If `{authentication-method}` is equal to `aws_iam`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.aws-iam.aws-path` | `{aws-path}`
| `spring.cloud.vault.aws-iam.endpoint-uri` | `{aws-sts-endpoint-uri}`
| `spring.cloud.vault.aws-iam.role` | `{role}`
| `spring.cloud.vault.aws-iam.server-id` | `{aws-iam-server-id}`

If `{authentication-method}` is equal to `azure_msi`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.azure-msi.azure-path` | `{azure-path}`
| `spring.cloud.vault.azure-msi.role` | `{role}`

If `{authentication-method}` is equal to `cert`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.ssl.cert-auth-path` | `{cert-auth-path}`
| `spring.cloud.vault.ssl.key-store-password` | `{key-store-password}`
| `spring.cloud.vault.ssl.key-store` | `${SERVICE_BINDING_ROOT}/{name}/keystore.jks`

If `{authentication-method}` is equal to `cubbyhole`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.token` | `{token}`

If `{authentication-method}` is equal to `gcp_gce`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.gcp-gce.gcp-path` | `{gcp-path}`
| `spring.cloud.vault.gcp-gce.role` | `{role}`
| `spring.cloud.vault.gcp-gce.service-account` | `{gcp-service-account}`


If `{authentication-method}` is equal to `gcp_iam`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.gcp-iam.credentials.encoded-key` | `{encoded-key}`
| `spring.cloud.vault.gcp-iam.credentials.location` | `${SERVICE_BINDING_ROOT}/{name}/credentials.json`
| `spring.cloud.vault.gcp-iam.gcp-path` | `{gcp-path}`
| `spring.cloud.vault.gcp-iam.jwt-validity` | `{jwt-validity}`
| `spring.cloud.vault.gcp-iam.project-id` | `{gcp-project-id}`
| `spring.cloud.vault.gcp-iam.role` | `{role}`
| `spring.cloud.vault.gcp-iam.service-account` | `{gcp-service-account}`

If `{authentication-method}` is equal to `kubernetes`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.kubernetes.kubernetes-path` | `{kubernetes-path}`
| `spring.cloud.vault.kubernetes.role` | `{role}`

If `{authentication-method}` is equal to `token`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.token` | `{token}`


### Wavefront

Type: `wavefront`
Disable Property: `org.springframework.cloud.bindings.boot.wavefront.enable`

| Property | Value
| -------- | ------------------
| `management.metrics.export.wavefront.api-token` | `{api-token}`
| `management.metrics.export.wavefront.uri` | `{uri}`


## Extending Spring Boot Configuration

Consumers can extend the `BindingSpecificEnvironmentPostProcessor` with support for additional bindings by registering additional implementations of the `BindingsPropertiesProcessor`.

Within the `process` method, custom processors should make desired modifications to the application properties, using the contents of the bindings to compute property values as appropriate. Custom processors are strongly encouraged to use the `type` of each binding to filter for bindings intended for that processor.

Below is an example that processes a single binding of `type` `myservice`. If such a binding exists this processor sets `my.service.enabled=true` and sets `my.service.uri` to the value of `uri` found in the binding secret.

```
package com.example;


import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.boot.BindingsPropertiesProcessor;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

public final class MyServiceBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    public static final String TYPE = "myservice";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!environment.getProperty("com.example.bindings.myservice.enable", Boolean.class, true)) {
            return;
        }
        List<Binding> myBindings = bindings.filterBindings(TYPE);
        if (myBindings.size() == 0) {
            return;
        }
        properties.put("my.service.uri", myBindings.get(0).getSecret().get("uri"));
        properties.put("my.service.enabled", true);
    }

}
```

These lines from the above example allow users to set `com.example.bindings.myservice.enable=false` to disable the processor entirely:
```
        if (!environment.getProperty("com.example.bindings.myservice.enable", Boolean.class, true)) {
            return;
        }
```

You must add an entry in `META_INF/spring.factories` so that your custom processor can be discovered.
```
org.springframework.cloud.bindings.boot.BindingsPropertiesProcessor=com.example.MyServiceBindingsPropertiesProcessor
```

## License
This buildpack is released under version 2.0 of the [Apache License][a].

[a]: http://www.apache.org/licenses/LICENSE-2.0
