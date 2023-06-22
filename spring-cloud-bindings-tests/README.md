# Spring Cloud Bindings Test Boot 3

## Purpose of this module

For several Spring Cloud Bindings integrations, the properties bindings need to be different between Spring Boot 2 and Spring Boot 3.

In a Spring Boot 3 application, the mapping needs to be different because of changes with Spring Data Redis:

| Property                 | Value    |
|--------------------------|----------|
| `spring.data.redis.port` | `{port}` |
| `spring.data.redis.host` | `{host}` |

This module runs an application, based on Spring Boot 3, and checks whether the `RedisConnectionFactory` properly picked up the `spring.data.redis.port` and `spring.data.redis.host` values.

Pay attention to the 2 environment variables set in the `pom.xml` that set `SERVICE_BINDING_ROOT` - they allow Spring Boot Bindings to load the proper configuration in `bindings/redis`