package org.springframework.cloud;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.io.ClassPathResource;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.Scanner;

@TestConfiguration
public class RedisServerTestConfiguration {

    private final RedisServer redisServer;

    public RedisServerTestConfiguration() throws IOException {
        try (Scanner scanner = new Scanner(new ClassPathResource("bindings/redis/port").getInputStream())) {
            int port = scanner.nextInt();
            this.redisServer = new RedisServer(port);
        }
    }

    @PostConstruct
    public void postConstruct() {
        redisServer.start();
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}