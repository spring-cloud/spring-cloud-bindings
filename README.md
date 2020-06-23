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

### LDAP
Kind: `LDAP`
Disable Property: `org.springframework.cloud.bindings.boot.ldap.enable`

| Property | Value
| -------- | ------------------
| `spring.ldap.base` | `{secret/base}`
| `spring.ldap.password` | `{secret/password}`
| `spring.ldap.urls` | `{secret/urls}`
| `spring.ldap.username` | `{secret/username}`

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

### SCS Eureka
Kind: `Eureka`
Disable Property: `org.springframework.cloud.bindings.boot.eureka.enable`

| Property | Value
| -------- | ------------------
| `eureka.client.oauth2.client-id` | `{secret/client-id}`
| `eureka.client.oauth2.access-token-uri` | `{secret/access-token-uri}`
| `eureka.client.region` | `default`
| `eureka.client.serviceUrl.defaultZone` | `{secret/uri}/eureka/`

## Spring Security OAuth2
Kind: `OAuth2`
Disable Property: `org.springframework.cloud.bindings.boot.oauth2.enable`

| Property | Value
| -------- | ------------------
| `spring.security.oauth2.client.registration.{name}.client-id` | `{secret/client-id}`
| `spring.security.oauth2.client.registration.{name}.client-secret` | `{secret/client-secret}`
| `spring.security.oauth2.client.registration.{name}.provider` | `{metadata/provider}`
| `spring.security.oauth2.client.provider.{metadata/provider}.issuer-uri` | `{secret/issuer-uri}`
| `spring.security.oauth2.client.provider.{metadata/provider}.authorization-uri` | `{secret/authorization-uri}`
| `spring.security.oauth2.client.provider.{metadata/provider}.token-uri` | `{secret/token-uri}`
| `spring.security.oauth2.client.provider.{metadata/provider}.user-info-uri` | `{secret/user-info-uri}`
| `spring.security.oauth2.client.provider.{metadata/provider}.user-info-authentication-method` | `{secret/user-info-authentication-method}`
| `spring.security.oauth2.client.provider.{metadata/provider}.jwk-set-uri` | `{secret/jwk-set-uri}`
| `spring.security.oauth2.client.provider.{metadata/provider}.user-name-attribute` | `{secret/user-name-attribute}`

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


### Vault
Kind: `Vault`
Disable Property: `org.springframework.cloud.bindings.boot.vault.enable`

Any Provider:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.authentication` | `{secret/method}`
| `spring.cloud.vault.namespace` | `{secret/namespace}`
| `spring.cloud.vault.uri` | `{secret/uri}`

If `{secret/method}` is equal to `approle`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.app-role.app-role-path` | `{secret/app-role-path}`
| `spring.cloud.vault.app-role.role-id` | `{secret/role-id}`
| `spring.cloud.vault.app-role.role` | `{secret/role}`
| `spring.cloud.vault.app-role.secret-id` | `{secret/secret-id}`

If `{secret/method}` is equal to `aws_ec2`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.aws-ec2.aws-ec2-path` | `{secret/aws-ec2-path}`
| `spring.cloud.vault.aws-ec2.identity-document` | `{secret/aws-ec2-identity-document}`
| `spring.cloud.vault.aws-ec2.nonce` | `{secret/nonce}`
| `spring.cloud.vault.aws-ec2.role` | `{secret/role}`

If `{secret/method}` is equal to `aws_iam`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.aws-iam.aws-path` | `{secret/aws-path}`
| `spring.cloud.vault.aws-iam.endpoint-uri` | `{secret/endpoint-uri}`
| `spring.cloud.vault.aws-iam.role` | `{secret/token}`
| `spring.cloud.vault.aws-iam.server-id` | `{secret/server-id}`

If `{secret/method}` is equal to `azure_msi`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.azure-msi.azure-path` | `{secret/azure-path}`
| `spring.cloud.vault.azure-msi.role` | `{secret/role}`

If `{secret/method}` is equal to `cert`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.ssl.cert-auth-path` | `{secret/cert-auth-path}`
| `spring.cloud.vault.ssl.key-store-password` | `{secret/key-store-password}`
| `spring.cloud.vault.ssl.key-store` | `${CNB_BINDINGS}/{name}/secret/keystore.jks`

If `{secret/method}` is equal to `cubbyhole`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.token` | `{secret/token}`

If `{secret/method}` is equal to `gcp_gce`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.gcp-gce.gcp-path` | `{secret/gcp-path}`
| `spring.cloud.vault.gcp-gce.role` | `{secret/role}`
| `spring.cloud.vault.gcp-gce.service-account` | `{secret/service-account}`


If `{secret/method}` is equal to `gcp_iam`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.gcp-iam.credentials.encoded-key` | `{secret/encoded-key}`
| `spring.cloud.vault.gcp-iam.credentials.location` | `${CNB_BINDINGS}/{name}/secret/credentials.json`
| `spring.cloud.vault.gcp-iam.gcp-path` | `{secret/gcp-path}`
| `spring.cloud.vault.gcp-iam.jwt-validity` | `{secret/jwt-validity}`
| `spring.cloud.vault.gcp-iam.project-id` | `{secret/project-id}`
| `spring.cloud.vault.gcp-iam.role` | `{secret/role}`
| `spring.cloud.vault.gcp-iam.service-account-id` | `{secret/service-account-id}`

If `{secret/method}` is equal to `kubernetes`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.kubernetes.kubernetes-path` | `{secret/kubernetes-path}`
| `spring.cloud.vault.kubernetes.role` | `{secret/role}`

If `{secret/method}` is equal to `token`:
| Property | Value
| -------- | ------------------
| `spring.cloud.vault.token` | `{secret/token}`


### Wavefront

Kind: `Wavefront`
Disable Property: `org.springframework.cloud.bindings.boot.wavefront.enable`

| Property | Value
| -------- | ------------------
| `management.metrics.export.wavefront.api-token` | `{secret/api-token}`
| `management.metrics.export.wavefront.uri` | `{secret/uri}`


## Extending Spring Boot Configuration

Consumers can extend the `BindingSpecificEnvironmentPostProcessor` with support for additional bindings by registering additional implementations of the `BindingsPropertiesProcessor`.

Within the `process` method, custom processors should make desired modifications to the application properties, using the contents of the bindings to compute property values as appropriate. Custom processors are strongly encouraged to use the `kind` of each binding to filter for bindings intended for that processor.

Below is an example that processes a single binding of `kind` `myservice`. If such a binding exists this processor sets `my.service.enabled=true` and sets `my.service.uri` to the value of `uri` found in the binding secret.

```
package com.example;


import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.boot.BindingsPropertiesProcessor;
import org.springframework.core.env.Environment;

import java.util.List;
import java.util.Map;

public final class MyServiceBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    public static final String KIND = "myservice";

    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!environment.getProperty("com.example.bindings.myservice.enable", Boolean.class, true)) {
            return;
        }
        List<Binding> myBindings = bindings.filterBindings(KIND);
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
