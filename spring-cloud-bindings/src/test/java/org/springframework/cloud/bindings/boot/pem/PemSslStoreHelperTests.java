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

import java.nio.file.Path;
import java.security.KeyStore;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for {@link PemSslStoreHelper}.
 *
 */
class PemSslStoreHelperTests {
	@Test
	void createKeyStoreFileWhenNullValues() {
		assertThrows(java.lang.IllegalStateException.class, () -> {
			PemSslStoreHelper.createKeyStoreFile("key", "secret",null, null, "some-alias");
		});
	}

	@Test
	void createKeyStoreFileWhenHasKeyStoreDetailsCertAndKey() throws Exception {
        Path path = PemSslStoreHelper.createKeyStoreFile("key", "secret", "classpath:pem/test-cert.pem", "classpath:pem/test-key.pem", "some-alias");
        KeyStore keyStore = KeyStore.getInstance(path.toFile(), "secret".toCharArray());
		assertDoesNotThrow(() -> {
			assertThat(keyStore).isNotNull();
			assertThat(keyStore.getType()).isEqualTo("PKCS12");
			assertThat(keyStore.containsAlias("some-alias")).isTrue();
			assertThat(keyStore.getCertificate("some-alias")).isNotNull();
			assertThat(keyStore.getKey("some-alias", new char[]{})).isNotNull();
		});
	}

	@Test
	void createKeyStoreFileWhenHasTrustStoreDetailsWithoutKey() throws Exception {
		Path path = PemSslStoreHelper.createKeyStoreFile("trust", "secret", "classpath:pem/test-cert.pem", null, null);
        KeyStore keyStore = KeyStore.getInstance(path.toFile(), "secret".toCharArray());
        assertDoesNotThrow(() -> {
			assertThat(keyStore).isNotNull();
			assertThat(keyStore.getType()).isEqualTo("PKCS12");
			assertThat(keyStore.containsAlias("ssl-0")).isTrue();
			assertThat(keyStore.getCertificate("ssl-0")).isNotNull();
			assertThat(keyStore.getKey("ssl-0", new char[]{})).isNull();
		});
	}

	@Test
	void generatePassword()  {
		String s = PemSslStoreHelper.generatePassword();
		assertThat(s).isNotNull();
		assertThat(s.length()).isEqualTo(10);
		System.out.println(s);
	}
}
