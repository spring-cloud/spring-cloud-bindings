package org.springframework.cloud;
import org.springframework.boot.test.context.TestConfiguration;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@TestConfiguration
public class RedisServerTestConfiguration {

    private RedisServer redisServer;

    public RedisServerTestConfiguration() throws IOException {
        //this value should be set as well under bindings/redis/port so that the client configuration matches the server
        this.redisServer = new RedisServer(4242);
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