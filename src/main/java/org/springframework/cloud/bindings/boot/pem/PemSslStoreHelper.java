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
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * helper for creating stores from PEM-encoded certificates and private keys.
 */
public class PemSslStoreHelper {
	private static final String DEFAULT_KEY_ALIAS = "ssl";
	public static KeyStore createKeyStore(String name, String storeType, String certificate, String privateKey, String keyAlias) {
		try {
			Assert.notNull(certificate, "CertificateContent must not be null");
			String type = StringUtils.hasText(storeType) ? storeType : KeyStore.getDefaultType();
			KeyStore store = KeyStore.getInstance(type);
			store.load(null);
			String certificateContent = PemContent.load(certificate);
			String privateKeyContent = PemContent.load(privateKey);
			X509Certificate[] certificates = PemCertificateParser.parse(certificateContent);
			PrivateKey pk = PemPrivateKeyParser.parse(privateKeyContent);
			addCertificates(store, certificates, pk, keyAlias);
			return store;
		}
		catch (Exception ex) {
			throw new IllegalStateException(String.format("Unable to create %s store: %s", name, ex.getMessage()), ex);
		}
	}

	private static void addCertificates(KeyStore keyStore, X509Certificate[] certificates, PrivateKey privateKey, String keyAlias)
			throws KeyStoreException {
		String alias = (keyAlias != null) ? keyAlias : DEFAULT_KEY_ALIAS;
		if (privateKey != null) {
			keyStore.setKeyEntry(alias, privateKey, null, certificates);
		}
		else {
			for (int index = 0; index < certificates.length; index++) {
				keyStore.setCertificateEntry(alias + "-" + index, certificates[index]);
			}
		}
	}

}
