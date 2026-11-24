package org.springframework.cloud;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import static org.assertj.core.api.Assertions.assertThat;

class IntegrationTest {

	private static final String FLATTENED = "kubernetesServiceBindingFlattened";
	private static final String SPECIFIC = "kubernetesServiceBindingSpecific";
	private static final String[] SYSTEM_PROPERTIES = {
			"spring.data.redis.host",
			"spring.application.json",
			"org.springframework.cloud.bindings.boot.enable",
			"org.springframework.cloud.bindings.boot.mongodb.enable"
	};

	@AfterEach
	void clearSystemProperties() {
		for (String property : SYSTEM_PROPERTIES) {
			System.clearProperty(property);
		}
	}

	@Test
	void addsFlattenedAndSpecificPropertySources() {
		try (ConfigurableApplicationContext context = run()) {
			ConfigurableEnvironment env = context.getEnvironment();
			assertThat(env.getPropertySources().contains(FLATTENED)).isTrue();
			assertThat(env.getPropertySources().contains(SPECIFIC)).isTrue();
		}
	}

	@Test
	void mapsRedisBindingToSpringDataRedis() {
		try (ConfigurableApplicationContext context = run()) {
			ConfigurableEnvironment env = context.getEnvironment();
			assertThat(env.getProperty("spring.data.redis.host")).isEqualTo("localhost");
			assertThat(env.getProperty("spring.data.redis.port")).isEqualTo("4343");
		}
	}

	@Test
	void mapsMongoBindingToBoot3AndBoot4Prefixes() {
		try (ConfigurableApplicationContext context = run()) {
			ConfigurableEnvironment env = context.getEnvironment();
			for (String prefix : new String[] { "spring.data.mongodb.", "spring.mongodb." }) {
				assertThat(env.getProperty(prefix + "host")).isEqualTo("mongo.example");
				assertThat(env.getProperty(prefix + "port")).isEqualTo("27017");
				assertThat(env.getProperty(prefix + "database")).isEqualTo("mydb");
				assertThat(env.getProperty(prefix + "username")).isEqualTo("u");
				assertThat(env.getProperty(prefix + "password")).isEqualTo("p");
			}
		}
	}

	@Test
	void exposesFlattenedKeysForPlaceholders() {
		try (ConfigurableApplicationContext context = run()) {
			ConfigurableEnvironment env = context.getEnvironment();
			assertThat(env.getProperty("k8s.bindings.redis.host")).isEqualTo("localhost");
			assertThat(env.getProperty("k8s.bindings.mongodb.host")).isEqualTo("mongo.example");
			assertThat(env.getProperty("test.redis.host")).isEqualTo("localhost");
		}
	}

	@Test
	void bindingOverridesApplicationProperties() {
		try (ConfigurableApplicationContext context = run()) {
			assertThat(context.getEnvironment().getProperty("spring.data.redis.host")).isEqualTo("localhost");
		}
	}

	@Test
	void bindingOverridesSystemProperty() {
		System.setProperty("spring.data.redis.host", "sys");
		try (ConfigurableApplicationContext context = run()) {
			assertThat(context.getEnvironment().getProperty("spring.data.redis.host")).isEqualTo("localhost");
		}
	}

	@Test
	void bindingOverridesSpringApplicationJson() {
		System.setProperty("spring.application.json", "{\"spring.data.redis.host\":\"json\"}");
		try (ConfigurableApplicationContext context = run()) {
			assertThat(context.getEnvironment().getProperty("spring.data.redis.host")).isEqualTo("localhost");
		}
	}

	@Test
	void commandLineOverridesBinding() {
		try (ConfigurableApplicationContext context = run("--spring.data.redis.host=cli")) {
			assertThat(context.getEnvironment().getProperty("spring.data.redis.host")).isEqualTo("cli");
		}
	}

	@Test
	void globalSwitchDisablesSpecificBindingsOnly() {
		System.setProperty("org.springframework.cloud.bindings.boot.enable", "false");
		try (ConfigurableApplicationContext context = run()) {
			ConfigurableEnvironment env = context.getEnvironment();
			// the flattened processor is not gated by the global switch
			assertThat(env.getPropertySources().contains(FLATTENED)).isTrue();
			assertThat(env.getPropertySources().contains(SPECIFIC)).isFalse();
			assertThat(env.getProperty("spring.data.redis.host")).isEqualTo("fromfile");
			assertThat(env.getProperty("k8s.bindings.redis.host")).isEqualTo("localhost");
		}
	}

	@Test
	void mongoSwitchDisablesOnlyMongo() {
		System.setProperty("org.springframework.cloud.bindings.boot.mongodb.enable", "false");
		try (ConfigurableApplicationContext context = run()) {
			ConfigurableEnvironment env = context.getEnvironment();
			assertThat(env.containsProperty("spring.data.mongodb.host")).isFalse();
			assertThat(env.containsProperty("spring.mongodb.host")).isFalse();
			assertThat(env.getProperty("spring.data.redis.host")).isEqualTo("localhost");
		}
	}

	private ConfigurableApplicationContext run(String... args) {
		return new SpringApplicationBuilder(BindingsTestApplication.class)
				.web(WebApplicationType.NONE)
				.run(args);
	}
}
