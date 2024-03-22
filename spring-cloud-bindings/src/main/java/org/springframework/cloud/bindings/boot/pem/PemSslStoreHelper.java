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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Random;

import org.springframework.util.Assert;

/**
 * helper for creating stores from PEM-encoded certificates and private keys.
 */
public class PemSslStoreHelper {
	public static final String PKCS12_STORY_TYPE = "PKCS12";
	private static final String DEFAULT_KEY_ALIAS = "ssl";

	/**
	 * Utility method to create a KeyStore and save it in the tmp directory with give name.
	 * @param name the store file name
	 * @param password the store password
	 * @param certificate the certificate to add to the store
	 * @param privateKey the private key to add to the store
	 * @param keyAlias the alias
	 * @return the path which store file is saved
	 */
	public static Path createKeyStoreFile(String name, String password, String certificate, String privateKey, String keyAlias) {
		KeyStore store = createKeyStore(certificate, privateKey, keyAlias);

		Path path;
        try {
			path = Files.createTempFile(Paths.get(System.getProperty("java.io.tmpdir")), name, ".p12");
        } catch (IOException e) {
			throw new IllegalStateException("Unable to create " + name, e);
        }

        try (FileOutputStream fos = new FileOutputStream(path.toString())) {
			store.store(fos, password.toCharArray());
		} catch (KeyStoreException e) {
			throw new IllegalStateException("Unable to write " + name, e);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("Cryptographic algorithm not available", e);
		} catch (CertificateException e) {
			throw new IllegalStateException("Unable to process certificate", e);
		} catch (IOException e) {
			throw new IllegalStateException("Unable to create " + name, e);
		}
		return path;
	}

	/**
	 *  Generates a password to use for KeyStore and/or TrustStore
	 * @return the password
	 */
	public static String generatePassword() {
		return new Random().ints(97 /* letter a */, 122 /* letter z */ + 1)
				.limit(10)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	private static KeyStore createKeyStore(String certificate, String privateKey, String keyAlias) {
		try {
			Assert.notNull(certificate, "CertificateContent must not be null");
			KeyStore store = KeyStore.getInstance(PKCS12_STORY_TYPE);
			store.load(null);
			String certificateContent = PemContent.load(certificate);
			String privateKeyContent = PemContent.load(privateKey);
			X509Certificate[] certificates = PemCertificateParser.parse(certificateContent);
			PrivateKey pk = PemPrivateKeyParser.parse(privateKeyContent);
			addCertificates(store, certificates, pk, keyAlias);
			return store;
		}
		catch (Exception ex) {
			throw new IllegalStateException(String.format("Unable to create key/trust store: %s", ex.getMessage()), ex);
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
