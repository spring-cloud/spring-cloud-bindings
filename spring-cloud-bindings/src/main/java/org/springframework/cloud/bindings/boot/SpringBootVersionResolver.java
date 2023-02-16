package org.springframework.cloud.bindings.boot;

import org.springframework.boot.SpringBootVersion;

/**
 * This class is used to allow, in unit tests for example,
 * to fake the resolution of which major version of spring boot is used
 */
class SpringBootVersionResolver {

    private int forcedVersion = -1;

    SpringBootVersionResolver() {
    }

    protected SpringBootVersionResolver(int forcedVersion) {
        this.forcedVersion = forcedVersion;
    }

   protected boolean isBootMajorVersionEnabled(int bootVersion) {
        if (forcedVersion != -1) {
            return forcedVersion == bootVersion;
        }
        int major = Integer.parseInt(SpringBootVersion.getVersion().split("\\.")[0]);
        return major == bootVersion;
    }
}
