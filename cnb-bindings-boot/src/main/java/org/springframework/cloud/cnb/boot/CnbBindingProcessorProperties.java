/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.cnb.boot;

/**
 * Properties describing a CfEnvProcessor, mainly used for better logging messages in {@link CnbBindingProcessor}.
 *
 * @author Mark Pollack
 */
public class CnbBindingProcessorProperties {

	private String propertyPrefixes;

	private String serviceName;

	private CnbBindingProcessorProperties() {
	}

	public static Builder builder() {
		return new CnbBindingProcessorProperties.Builder();
	}

	/**
	 * A string containing the values of property prefixes that will be set in the {@code process} method.  Used
	 * for logging purposes.
	 * @return property prefix values set in the {@code process} method
	 */
	public String getPropertyPrefixes() {
		return propertyPrefixes;
	}

	/**
	 * A name that describes the service being processed, eg. 'Redis', 'MongoDB'.  Used for logging purposes.
	 * @return name that describes the service being processed
	 */
	public String getServiceName() {
		return serviceName;
	}


	public static class Builder {
		private CnbBindingProcessorProperties processorProperties = new CnbBindingProcessorProperties();

		public Builder propertyPrefixes(String propertyPrefixes) {
			this.processorProperties.propertyPrefixes = propertyPrefixes;
			return this;
		}

		public Builder serviceName(String serviceName) {
			this.processorProperties.serviceName = serviceName;
			return this;
		}

		public CnbBindingProcessorProperties build() {
			return processorProperties;
		}
	}
}
