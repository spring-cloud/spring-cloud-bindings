# Spring Cloud Bindings
The Spring Cloud Bindings library exposes a rich Java language binding for the [Kubernetes Service Binding Specification][s].  In addition, if opted-in, it configures Spring Boot application configuration properties appropriate for the type of binding encountered.

[s]: https://github.com/servicebinding/spec

## Features
* Java 17
* Spring Boot 3

For Spring Boot 2 compatibility, please have a look at the [1.x releases](https://github.com/spring-cloud/spring-cloud-bindings/tree/1.x).

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

### ActiveMQ Artemis
Type: `artemis`
Disable Property: `org.springframework.cloud.bindings.boot.artemis.enable`

#### Properties

| Property                                            | Value                                  |
| --------------------------------------------------- | -------------------------------------- |
| `spring.artemis.broker-url`                         | `{broker-url}`                         |
| `spring.artemis.mode`                               | `{mode}`                               |
| `spring.artemis.password`                           | `{password}`                           |
| `spring.artemis.user`                               | `{user}`                               |
| `spring.artemis.embedded.cluster-password`          | `{embedded.cluster-password}`          |
| `spring.artemis.embedded.data-directory`            | `{embedded.data-directory}`            |
| `spring.artemis.embedded.enabled`                   | `{embedded.enabled}`                   |
| `spring.artemis.embedded.persistent`                | `{embedded.persistent}`                |
| `spring.artemis.embedded.queues`                    | `{embedded.queues}`                    |
| `spring.artemis.embedded.server-id`                 | `{embedded.server-id}`                 |
| `spring.artemis.embedded.topics`                    | `{embedded.topics}`                    |
| `spring.artemis.pool.block-if-full`                 | `{pool.block-if-full}`                 |
| `spring.artemis.pool.block-if-full-timeout`         | `{pool.block-if-full-timeout}`         |
| `spring.artemis.pool.enabled`                       | `{pool.enabled}`                       |
| `spring.artemis.pool.idle-timeout`                  | `{pool.idle-timeout}`                  |
| `spring.artemis.pool.max-connections`               | `{pool.max-connections}`               |
| `spring.artemis.pool.max-sessions-per-connection`   | `{pool.max-sessions-per-connection}`   |
| `spring.artemis.pool.time-between-expiration-check` | `{pool.time-between-expiration-check}` |
| `spring.artemis.pool.use-anonymous-producers`       | `{pool.use-anonymous-producers}`       |

### Cassandra
Type: `cassandra`
Disable Property: `org.springframework.cloud.bindings.boot.cassandra.enable`

#### Properties

| Property                                                     | Value                                         |
|--------------------------------------------------------------|-----------------------------------------------|
| `spring.cassandra.cluster-name`                              | `{cluster-name}`                              |
| `spring.cassandra.compression`                               | `{compression}`                               |
| `spring.cassandra.contact-points`                            | `{contact-points}`                            |
| `spring.cassandra.keyspace-name`                             | `{keyspace-name}`                             |
| `spring.cassandra.password`                                  | `{password}`                                  |
| `spring.cassandra.port`                                      | `{port}`                                      |
| `spring.cassandra.ssl`                                       | `{ssl}`                                       |
| `spring.cassandra.username`                                  | `{username}`                                  |
| `spring.cassandra.request.throttler.drain-interval`          | `{request.throttler.drain-interval}`          |
| `spring.cassandra.request.throttler.max-concurrent-requests` | `{request.throttler.max-concurrent-requests}` |
| `spring.cassandra.request.throttler.max-queue-size`          | `{request.throttler.max-queue-size}`          |
| `spring.cassandra.request.throttler.max-requests-per-second` | `{request.throttler.max-requests-per-second}` |

### Couchbase
Type: `couchbase`
Disable Property: `org.springframework.cloud.bindings.boot.couchbase.enable`

#### Properties

| Property                                         | Value                     |
|--------------------------------------------------|---------------------------|
| `spring.couchbase.connection-string`             | `{connection-string}`     |
| `spring.data.couchbase.bucket-name`              | `{bucket-name}`           |
| `spring.couchbase.password`                      | `{password}`              |
| `spring.couchbase.username`                      | `{username}`              |

### DB2 RDBMS
Type: `db2`
Disable Property: `org.springframework.cloud.bindings.boot.db2.enable`

| Property                              | Value                                                                                                                                        |
| ------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| `spring.datasource.driver-class-name` | `com.ibm.db2.jcc.DB2Driver`                                                                                                                  |
| `spring.datasource.password`          | `{password}`                                                                                                                                 |
| `spring.datasource.url`               | `{jdbc-url}` or if not set then `jdbc:db2://{host}:{port}/{database}` (you must have host, port and database set or no mapping will occur)   |
| `spring.datasource.username`          | `{username}`                                                                                                                                 |
| `spring.r2dbc.url`                    | `{r2dbc-url}` or if not set then `r2dbc:db2://{host}:{port}/{database}` (you must have host, port and database set or no mapping will occur) |
| `spring.r2dbc.password`               | `{password}`                                                                                                                                 |
| `spring.r2dbc.username`               | `{username}`                                                                                                                                 |


### Elasticsearch
Type: `elasticsearch`
Disable Property: `org.springframework.cloud.bindings.boot.elasticsearch.enable`

#### Properties

| Property                        | Value         |
|---------------------------------|---------------|
| `spring.elasticsearch.password` | `{password}`  |
| `spring.elasticsearch.uris`     | `{uris}`      |
| `spring.elasticsearch.username` | `{username}`  |

### Kafka
Type: `kafka`
Disable Property: `org.springframework.cloud.bindings.boot.kafka.enable`

| Property                                  | Value                          |
| ----------------------------------------- | ------------------------------ |
| `spring.kafka.bootstrap-servers`          | `{bootstrap-servers}`          |
| `spring.kafka.consumer.bootstrap-servers` | `{consumer.bootstrap-servers}` |
| `spring.kafka.producer.bootstrap-servers` | `{producer.bootstrap-servers}` |
| `spring.kafka.streams.bootstrap-servers`  | `{streams.bootstrap-servers}`  |

### LDAP
Type: `ldap`
Disable Property: `org.springframework.cloud.bindings.boot.ldap.enable`

| Property               | Value        |
| ---------------------- | ------------ |
| `spring.ldap.base`     | `{base}`     |
| `spring.ldap.password` | `{password}` |
| `spring.ldap.urls`     | `{urls}`     |
| `spring.ldap.username` | `{username}` |

### MongoDB
Type: `mongodb`
Disable Property: `org.springframework.cloud.bindings.boot.mongodb.enable`

| Property                                      | Value                       |
| --------------------------------------------- | --------------------------- |
| `spring.data.mongodb.authentication-database` | `{authentication-database}` |
| `spring.data.mongodb.database`                | `{database}`                |
| `spring.data.mongodb.gridfs.database"`        | `{grid-fs-database}`        |
| `spring.data.mongodb.host`                    | `{host}`                    |
| `spring.data.mongodb.password`                | `{password}`                |
| `spring.data.mongodb.port`                    | `{port}`                    |
| `spring.data.mongodb.uri`                     | `{uri}`                     |
| `spring.data.mongodb.username`                | `{username}`                |

### MySQL RDBMS
Type: `mysql`
Disable Property: `org.springframework.cloud.bindings.boot.mysql.enable`

| Property                              | Value                                                                                                                                                                                                              |
| ------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| `spring.datasource.driver-class-name` | `org.mariadb.jdbc.Driver` or `com.mysql.cj.jdbc.Driver` depending on classpath                                                                                                                                     |
| `spring.datasource.password`          | `{password}`                                                                                                                                                                                                       |
| `spring.datasource.url`               | `{jdbc-url}` or if not set then `jdbc:mysql://{host}:{port}/{database}` or `jdbc:mariadb://{host}:{port}/{database}` depending on classpath (you must have host, port and database set or no mapping will occur)   |
| `spring.datasource.username`          | `{username}`                                                                                                                                                                                                       |
| `spring.r2dbc.url`                    | `{r2dbc-url}` or if not set then `r2dbc:mysql://{host}:{port}/{database}` or `r2dbc:mariadb//{host}:{port}/{database}` depending on classpath (you must have host, port and database set or no mapping will occur) |
| `spring.r2dbc.password`               | `{password}`                                                                                                                                                                                                       |
| `spring.r2dbc.username`               | `{username}`                                                                                                                                                                                                       |

**Note:** Libraries on the classpath are examined for the purpose of evaluating the appropriate `jdbc` and `r2dbc` URLs.  The existence of both MySQL and MariaDB libraries on the classpath is not supported and may lead to non-deterministic results.

### Neo4J
Type: `neo4j`
Disable Property: `org.springframework.cloud.bindings.boot.neo4j.enable`

#### Properties

| Property                                | Value          |
|-----------------------------------------|----------------|
| `spring.neo4j.uri`                      | `{uri}`        |
| `spring.neo4j.authentication.username`  | `{username}`   |
| `spring.neo4j.authentication.password`  | `{password}`   |

### Oracle RDBMS
Type: `oracle`
Disable Property: `org.springframework.cloud.bindings.boot.oracle.enable`

| Property                              | Value                                                                                                                                           |
| ------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| `spring.datasource.driver-class-name` | `oracle.jdbc.OracleDriver`                                                                                                                      |
| `spring.datasource.password`          | `{password}`                                                                                                                                    |
| `spring.datasource.url`               | `{jdbc-url}` or if not set then `jdbc:oracle://{host}:{port}/{database}` (you must have host, port and database set or no mapping will occur)   |
| `spring.datasource.username`          | `{username}`                                                                                                                                    |
| `spring.r2dbc.url`                    | `{r2dbc-url}` or if not set then `r2dbc:oracle://{host}:{port}/{database}` (you must have host, port and database set or no mapping will occur) |
| `spring.r2dbc.password`               | `{password}`                                                                                                                                    |
| `spring.r2dbc.username`               | `{username}`                                                                                                                                    |

### PostgreSQL RDBMS
Type: `postgresql`
Disable Property: `org.springframework.cloud.bindings.boot.postgresql.enable`

| Property                              | Value                                                                                                                                                                                                                                                                              |
| ------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `spring.datasource.driver-class-name` | `org.postgresql.Driver`                                                                                                                                                                                                                                                            |
| `spring.datasource.password`          | `{password}`                                                                                                                                                                                                                                                                       |
| `spring.datasource.url`               | `{jdbc-url}` or if not set then `jdbc:postgresql://{host}:{port}/{database}`. If a binding for `{sslmode}`, `{sslrootcert}`, or `{options}` are set, the value is appended as properties to the generated URL (you must have host, port and database set or no mapping will occur)   |
| `spring.datasource.username`          | `{username}`                                                                                                                                                                                                                                                                       |
| `spring.r2dbc.url`                    | `{r2dbc-url}` or if not set then `r2dbc:postgresql://{host}:{port}/{database}`. If a binding for `{sslmode}`, `{sslrootcert}`, or `{options}` are set, the value is appended as properties to the generated URL (you must have host, port and database set or no mapping will occur) |
| `spring.r2dbc.password`               | `{password}`                                                                                                                                                                                                                                                                       |
| `spring.r2dbc.username`               | `{username}`                                                                                                                                                                                                                                                                       |

### RabbitMQ
Type: `rabbitmq`
Disable Property: `org.springframework.cloud.bindings.boot.rabbitmq.enable`

| Property                       | Value            |
| ------------------------------ | ---------------- |
| `spring.rabbitmq.addresses`    | `{addresses}`    |
| `spring.rabbitmq.host`         | `{host}`         |
| `spring.rabbitmq.password`     | `{password}`     |
| `spring.rabbitmq.port`         | `{port}`         |
| `spring.rabbitmq.username`     | `{username}`     |
| `spring.rabbitmq.virtual-host` | `{virtual-host}` |

### Redis
Type: `redis`
Disable Property: `org.springframework.cloud.bindings.boot.redis.enable`

#### Properties

| Property                                  | Value                     |
|-------------------------------------------|---------------------------|
| `spring.data.redis.client-name`           | `{client-name}`           |
| `spring.data.redis.cluster.max-redirects` | `{cluster.max-redirects}` |
| `spring.data.redis.cluster.nodes`         | `{cluster.nodes}`         |
| `spring.data.redis.database`              | `{database}`              |
| `spring.data.redis.host`                  | `{host}`                  |
| `spring.data.redis.password`              | `{password}`              |
| `spring.data.redis.port`                  | `{port}`                  |
| `spring.data.redis.sentinel.master`       | `{sentinel.master}`       |
| `spring.data.redis.sentinel.nodes`        | `{sentinel.nodes}`        |
| `spring.data.redis.ssl`                   | `{ssl}`                   |
| `spring.data.redis.url`                   | `{url}`                   |

### SAP Hana
Type: `hana`
Disable Property: `org.springframework.cloud.bindings.boot.hana.enable`

| Property                              | Value                                                                                                                                        |
| ------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| `spring.datasource.driver-class-name` | `com.sap.db.jdbc.Driver`                                                                                                                     |
| `spring.datasource.password`          | `{password}`                                                                                                                                 |
| `spring.datasource.url`               | `{jdbc-url}` or if not set then `jdbc:sap://{host}:{port}/{database}` (you must have host, port and database set or no mapping will occur)   |
| `spring.datasource.username`          | `{username}`                                                                                                                                 |
| `spring.r2dbc.url`                    | `{r2dbc-url}` or if not set then `r2dbc:sap://{host}:{port}/{database}` (you must have host, port and database set or no mapping will occur) |
| `spring.r2dbc.password`               | `{password}`                                                                                                                                 |
| `spring.r2dbc.username`               | `{username}`                                                                                                                                 |

## SCS Config Server
Type: `config`
Disable Property: `org.springframework.cloud.bindings.boot.config.enable`

| Property                                           | Value                |
| -------------------------------------------------- | -------------------- |
| `spring.cloud.config.uri`                          | `{uri}`              |
| `spring.cloud.config.client.oauth2.clientId`       | `{client-id}`        |
| `spring.cloud.config.client.oauth2.clientSecret`   | `{client-secret}`    |
| `spring.cloud.config.client.oauth2.accessTokenUri` | `{access-token-uri}` |

## SCS Eureka

Type: `eureka`
Disable Property: `org.springframework.cloud.bindings.boot.eureka.enable`

| Property                                 | Value                                                              |
|------------------------------------------|--------------------------------------------------------------------|
| `eureka.client.oauth2.client-id`         | `{client-id}`                                                      |
| `eureka.client.oauth2.access-token-uri`  | `{access-token-uri}`                                               |
| `eureka.client.region`                   | `default`                                                          |
| `eureka.client.serviceUrl.defaultZone`   | `{uri}/eureka/`                                                    |
| `eureka.client.tls.enabled`              | `true` when `{ca.crt}` is set                                      |
| `eureka.client.tls.trust-store`          | derived from `{ca.crt}`                                            |
| `eureka.client.tls.trust-store-type`     | `"PKCS12"` when `{ca.crt}` is set                                  |
| `eureka.client.tls.trust-store-password` | random string when `{ca.crt}` is set                               |
| `eureka.instance.preferIpAddress`        | `true` when `{ca.crt}` is set[^1]                                  |
| `eureka.client.tls.key-alias`            | `"eureka"` when `{ca.crt}`, `{tls.crt}` and `{tls.key}` are set    |
| `eureka.client.tls.key-store`            | derived from `{tls.crt}` and `{tls.key}`                           |
| `eureka.client.tls.key-store-type`       | `"PKCS12"` when `{ca.crt}`, `{tls.crt}` and `{tls.key}` are set    |
| `eureka.client.tls.key-store-password`   | random string when `{ca.crt}`, `{tls.crt}` and `{tls.key}` are set |
| `eureka.client.tls.key-password`         | `""` when `{ca.crt}`, `{tls.crt}` and `{tls.key}` are set          |

> [^1]: Note that `eureka.instance.perferIpAddress` will not be overwritten by the Eureka auto-configuration if it is
> already set in the environment. Applications wishing to set an explicit endpoint with `eureka.instance.host` can
> set `eureka.instance.perferIpAddress` to `false` and it will not be overwritten.

## Spring Security OAuth2
Type: `oauth2`
Disable Property: `org.springframework.cloud.bindings.boot.oauth2.enable`

| Property                                                                            | Value                                                                                                                         |
| ----------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| `spring.security.oauth2.client.registration.{name}.client-id`                       | `{client-id}`                                                                                                                 |
| `spring.security.oauth2.client.registration.{name}.client-secret`                   | `{client-secret}`                                                                                                             |
| `spring.security.oauth2.client.registration.{name}.provider`                        | `{provider}`                                                                                                                  |
| `spring.security.oauth2.client.registration.{name}.client-name`                     | `{client-name}`                                                                                                               |
| `spring.security.oauth2.client.registration.{name}.client-authentication-method`    | `{client-authentication-method}`                                                                                              |
| `spring.security.oauth2.client.registration.{name}.authorization-grant-type`        | `{authorization-grant-type}` or if not set then `{authorization-grant-types}` if it contains only one value (comma-separated) |
| `spring.security.oauth2.client.registration.{name}.redirect-uri`                    | `{redirect-uri}` or if not set then `{redirect-uris}` if it contains only one value (comma-separated)                         |
| `spring.security.oauth2.client.registration.{name}.scope`                           | `{scope}`                                                                                                                     |
| `spring.security.oauth2.client.provider.{provider}.issuer-uri`                      | `{issuer-uri}`                                                                                                                |
| `spring.security.oauth2.client.provider.{provider}.authorization-uri`               | `{authorization-uri}`                                                                                                         |
| `spring.security.oauth2.client.provider.{provider}.token-uri`                       | `{token-uri}`                                                                                                                 |
| `spring.security.oauth2.client.provider.{provider}.user-info-uri`                   | `{user-info-uri}`                                                                                                             |
| `spring.security.oauth2.client.provider.{provider}.user-info-authentication-method` | `{user-info-authentication-method}`                                                                                           |
| `spring.security.oauth2.client.provider.{provider}.jwk-set-uri`                     | `{jwk-set-uri}`                                                                                                               |
| `spring.security.oauth2.client.provider.{provider}.user-name-attribute`             | `{user-name-attribute}`                                                                                                       |

### SQLServer RDBMS
Type: `sqlserver`
Disable Property: `org.springframework.cloud.bindings.boot.sqlserver.enable`

| Property                              | Value                                                                                                                                              |
| ------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| `spring.datasource.driver-class-name` | `com.microsoft.sqlserver.jdbc.SQLServerDriver`                                                                                                     |
| `spring.datasource.password`          | `{password}`                                                                                                                                       |
| `spring.datasource.url`               | `jdbc:sqlserver://{host}:{port}/{database}` (you must have host, port and database set or no mapping will occur)                                   |
| `spring.datasource.username`          | `{username}`                                                                                                                                       |
| `spring.r2dbc.url`                    | `{r2dbc-url}` or if not set then `r2dbc:sqlserver://{host}:{port}/{database}` (you must have host, port and database set or no mapping will occur) |
| `spring.r2dbc.password`               | `{password}`                                                                                                                                       |
| `spring.r2dbc.username`               | `{username}`                                                                                                                                       |


### Vault
Type: `vault`
Disable Property: `org.springframework.cloud.bindings.boot.vault.enable`

Any Provider:

| Property                            | Value                     |
| ----------------------------------- | ------------------------- |
| `spring.cloud.vault.authentication` | `{authentication-method}` |
| `spring.cloud.vault.namespace`      | `{namespace}`             |
| `spring.cloud.vault.uri`            | `{uri}`                   |

If `{authentication-method}` is equal to `approle`:

| Property                                    | Value             |
| ------------------------------------------- | ----------------- |
| `spring.cloud.vault.app-role.app-role-path` | `{app-role-path}` |
| `spring.cloud.vault.app-role.role-id`       | `{role-id}`       |
| `spring.cloud.vault.app-role.role`          | `{role}`          |
| `spring.cloud.vault.app-role.secret-id`     | `{secret-id}`     |

If `{authentication-method}` is equal to `aws_ec2`:

| Property                                       | Value                                  |
| ---------------------------------------------- | -------------------------------------- |
| `spring.cloud.vault.aws-ec2.aws-ec2-path`      | `{aws-ec2-path}`                       |
| `spring.cloud.vault.aws-ec2.identity-document` | `{aws-ec2-instance-identity-document}` |
| `spring.cloud.vault.aws-ec2.nonce`             | `{nonce}`                              |
| `spring.cloud.vault.aws-ec2.role`              | `{role}`                               |

If `{authentication-method}` is equal to `aws_iam`:

| Property                                  | Value                                  |
|-------------------------------------------|----------------------------------------|
| `spring.cloud.vault.aws-iam.aws-path`     | `{aws-path}`                           |
| `spring.cloud.vault.aws-iam.endpoint-uri` | `{aws-sts-endpoint-uri}`               |
| `spring.cloud.vault.aws-iam.role`         | `{role}`                               |
| `spring.cloud.vault.aws-iam.server-name`  | `{aws-iam-server-name}`                |

If `{authentication-method}` is equal to `azure_msi`:

| Property                                              | Value                                    |
|-------------------------------------------------------|------------------------------------------|
| `spring.cloud.vault.azure-msi.azure-path`             | `{azure-path}`                           |
| `spring.cloud.vault.azure-msi.role`                   | `{role}`                                 |
| `spring.cloud.vault.azure-msi.metadata-service`       | `{metadata-service}`                     |
| `spring.cloud.vault.azure-msi.identity-token-service` | `{identity-token-service}`               |

If `{authentication-method}` is equal to `cert`:

| Property                                      | Value                                                         |
|-----------------------------------------------|---------------------------------------------------------------|
| `spring.cloud.vault.ssl.cert-auth-path`       | `{cert-auth-path}`                                            |
| `spring.cloud.vault.ssl.key-store-password`   | `{key-store-password}`                                        |
| `spring.cloud.vault.ssl.key-store`            | `${SERVICE_BINDING_ROOT}/{name}/keystore.jks`                 |
| `spring.cloud.vault.ssl.trust-store`          | `${SERVICE_BINDING_ROOT}/{name}/truststore.jks`               |
| `spring.cloud.vault.ssl.trust-store-password` | `{trust-store-password}`                                      |

If `{authentication-method}` is equal to `cubbyhole`:

| Property                   | Value     |
| -------------------------- | --------- |
| `spring.cloud.vault.token` | `{token}` |

If `{authentication-method}` is equal to `gcp_gce`:

| Property                                     | Value                   |
| -------------------------------------------- | ----------------------- |
| `spring.cloud.vault.gcp-gce.gcp-path`        | `{gcp-path}`            |
| `spring.cloud.vault.gcp-gce.role`            | `{role}`                |
| `spring.cloud.vault.gcp-gce.service-account` | `{gcp-service-account}` |

If `{authentication-method}` is equal to `gcp_iam`:

| Property                                             | Value                                              |
|------------------------------------------------------|----------------------------------------------------|
| `spring.cloud.vault.gcp-iam.credentials.encoded-key` | `{encoded-key}`                                    |
| `spring.cloud.vault.gcp-iam.credentials.location`    | `${SERVICE_BINDING_ROOT}/{name}/credentials.json`  |
| `spring.cloud.vault.gcp-iam.gcp-path`                | `{gcp-path}`                                       |
| `spring.cloud.vault.gcp-iam.jwt-validity`            | `{jwt-validity}`                                   |
| `spring.cloud.vault.gcp-iam.project-id`              | `{gcp-project-id}`                                 |
| `spring.cloud.vault.gcp-iam.role`                    | `{role}`                                           |
| `spring.cloud.vault.gcp-iam.service-account`         | `{gcp-service-account}` Spring Boot 2              |
| `spring.cloud.vault.gcp-iam.service-account-id`      | `{gcp-service-account}` Spring Boot 3              |

If `{authentication-method}` is equal to `kubernetes`:

| Property                                                              | Value                                                   |
|-----------------------------------------------------------------------|---------------------------------------------------------|
| `spring.cloud.vault.kubernetes.role`                                  | `{role}`                                                |
| `spring.cloud.vault.kubernetes.kubernetes-path`                       | `{kubernetes-path}`                                     |
| `spring.cloud.vault.kubernetes.kubernetes-service-account-token-file` | `{kubernetes-service-account-token-file}` Spring Boot 3 |

If `{authentication-method}` is equal to `token`:

| Property                   | Value     |
| -------------------------- | --------- |
| `spring.cloud.vault.token` | `{token}` |


### Wavefront

Type: `wavefront`
Disable Property: `org.springframework.cloud.bindings.boot.wavefront.enable`

| Property                                        | Value         |
| ----------------------------------------------- | ------------- |
| `management.metrics.export.wavefront.api-token` | `{api-token}` |
| `management.metrics.export.wavefront.uri`       | `{uri}`       |


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
