/*
 * Copyright 2012-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.bindings.boot.pem;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link PemContent}.
 *
 * @author Phillip Webb
 */
class PemContentTests {

	@Test
	void loadWhenContentIsNullReturnsNull() {
		assertThat(PemContent.load(null)).isNull();
	}

	@Test
	void loadWhenContentIsPemContentReturnsContent() {
		String content = "-----BEGIN CERTIFICATE-----\n" +
						 "MIICpDCCAYwCCQCDOqHKPjAhCTANBgkqhkiG9w0BAQUFADAUMRIwEAYDVQQDDAls\n" +
						 "b2NhbGhvc3QwHhcNMTQwOTEwMjE0MzA1WhcNMTQxMDEwMjE0MzA1WjAUMRIwEAYD\n" +
						 "VQQDDAlsb2NhbGhvc3QwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDR\n" +
						 "0KfxUw7MF/8RB5/YXOM7yLnoHYb/M/6dyoulMbtEdKKhQhU28o5FiDkHcEG9PJQL\n" +
						 "gqrRgAjl3VmCC9omtfZJQ2EpfkTttkJjnKOOroXhYE51/CYSckapBYCVh8GkjUEJ\n" +
						 "uEfnp07cTfYZFqViIgIWPZyjkzl3w4girS7kCuzNdDntVJVx5F/EsFwMA8n3C0Qa\n" +
						 "zHQoM5s00Fer6aTwd6AW0JD5QkADavpfzZ554e4HrVGwHlM28WKQQkFzzGu44FFX\n" +
						 "yVuEF3HeyVPug8GRHAc8UU7ijVgJB5TmbvRGYowIErD5i4VvGLuOv9mgR3aVyN0S\n" +
						 "dJ1N7aJnXpeSQjAgf03jAgMBAAEwDQYJKoZIhvcNAQEFBQADggEBAE4yvwhbPldg\n" +
						 "Bpl7sBw/m2B3bfiNeSqa4tII1PQ7ysgWVb9HbFNKkriScwDWlqo6ljZfJ+SDFCoj\n" +
						 "bQz4fOFdMAOzRnpTrG2NAKMoJLY0/g/p7XO00PiC8T3h3BOJ5SHuW3gUyfGXmAYs\n" +
						 "DnJxJOrwPzj57xvNXjNSbDOJ3DRfCbB0CWBexOeGDiUokoEq3Gnz04Q4ZfHyAcpZ\n" +
						 "3deMw8Od5p9WAoCh3oClpFyOSzXYKZd+3ppMMtfc4wnbfocnfSFxj0UCpOEJw4Ez\n" +
						 "+lGuHKdhNOVW9CmqPD1y76o6c8PQKuF7KZEoY2jvy3GeIfddBvqXgZ4PbWvFz1jO\n" +
						 "32C9XWHwRA4=\n" +
						 "-----END CERTIFICATE-----";
		assertThat(PemContent.load(content)).isEqualTo(content);
	}

	@Test
	void loadWhenClasspathLocationReturnsContent() throws IOException {
		String actual = PemContent.load("classpath:pem/test-cert.pem");
		String expected = asString(new ClassPathResource("pem/test-cert.pem"));
		assertThat(actual).isEqualTo(expected);
	}

	@Test
	void loadWhenFileLocationReturnsContent() throws IOException {
		String actual = PemContent.load("src/test/resources/pem/test-cert.pem");
		String expected = asString(new ClassPathResource("pem/test-cert.pem"));
		assertThat(actual).isEqualTo(expected);
	}

	public static String asString(ClassPathResource resource) throws IOException {
		Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
		return FileCopyUtils.copyToString(reader);
	}
}
