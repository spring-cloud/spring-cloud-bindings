package org.springframework.cloud;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@Configuration
@EnableRedisRepositories
public class SpringBoot3Application {
}
