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

/**
 * SSL trust material provider for PEM-encoded certificates.
 *
 * This package is copied from org.springframework.boot.ssl.pem introduced into spring-boot in v3.1.0.
 * It is simplified somewhat here, and modified for Java 8 compatibility.
 * We copied it because the spring-cloud-bindings library needs to work with older versions of spring boot, and must be
 * Java 8 compatible.
 */
package org.springframework.cloud.bindings.boot.pem;
