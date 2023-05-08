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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

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
	void whenNullValues() {
		assertThrows(java.lang.IllegalStateException.class, () -> {
			PemSslStoreHelper.createKeyStore("key", "PKCS12", null, null, "some-alias");
		});
	}

	@Test
	void whenHasKeyStoreDetailsCertAndKey() {
		KeyStore keyStore = PemSslStoreHelper.createKeyStore("key", "PKCS12", "classpath:pem/test-cert.pem", "classpath:pem/test-key.pem", "some-alias");
		assertDoesNotThrow(() -> {
			assertThat(keyStore).isNotNull();
			assertThat(keyStore.getType()).isEqualTo("PKCS12");
			assertThat(keyStore.containsAlias("some-alias")).isTrue();
			assertThat(keyStore.getCertificate("some-alias")).isNotNull();
			assertThat(keyStore.getKey("some-alias", new char[]{})).isNotNull();
		});
	}

	@Test
	void whenHasTrustStoreDetailsWithoutKey() throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
		KeyStore keyStore = PemSslStoreHelper.createKeyStore("trust", "PKCS12", "classpath:pem/test-cert.pem", null, null);
		assertDoesNotThrow(() -> {
			assertThat(keyStore).isNotNull();
			assertThat(keyStore.getType()).isEqualTo("PKCS12");
			assertThat(keyStore.containsAlias("ssl-0")).isTrue();
			assertThat(keyStore.getCertificate("ssl-0")).isNotNull();
			assertThat(keyStore.getKey("ssl-0", new char[]{})).isNull();
		});
	}
}
