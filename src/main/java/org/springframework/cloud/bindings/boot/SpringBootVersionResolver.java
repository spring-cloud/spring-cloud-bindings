package org.springframework.cloud.bindings.boot;

import org.springframework.boot.SpringBootVersion;

public class SpringBootVersionResolver {

    private int forcedVersion;

    public SpringBootVersionResolver() {
    }

    protected SpringBootVersionResolver(int forcedVersion) {
        this.forcedVersion = forcedVersion;
    }

    public boolean isBootMajorVersionEnabled(int bootVersion) {
        if (forcedVersion != 0) {
            return forcedVersion == bootVersion;
        }
        int major = Integer.parseInt(SpringBootVersion.getVersion().split("\\.")[0]);
        return major == bootVersion;
    }
}
