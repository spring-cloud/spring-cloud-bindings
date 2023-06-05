package org.springframework.cloud;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = RedisServerTestConfiguration.class)
public class IntegrationTest {

	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void shouldSaveUser_toRedis() {
		redisTemplate.opsForValue().set("hello", "world");
		assertTrue(redisTemplate.hasKey("hello"));
	}
}
