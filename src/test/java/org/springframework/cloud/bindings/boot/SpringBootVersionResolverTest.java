package org.springframework.cloud.bindings.boot;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpringBootVersionResolverTest {

    @Test
    @DisplayName("Should return Spring Boot 2 as Major Version since it's the provided runtime")
    void isBootMajorVersionEnabled() {
        SpringBootVersionResolver springBootVersionResolver = new SpringBootVersionResolver();
        assertTrue(springBootVersionResolver.isBootMajorVersionEnabled(2));
        assertFalse(springBootVersionResolver.isBootMajorVersionEnabled(3));
    }

    @Test
    @DisplayName("Should return the forced version 2")
    void isBootMajorVersionEnabled_forced_version2() {
        SpringBootVersionResolver springBootVersionResolver = new SpringBootVersionResolver(2);
        assertTrue(springBootVersionResolver.isBootMajorVersionEnabled(2));
        assertFalse(springBootVersionResolver.isBootMajorVersionEnabled(3));
    }

    @Test
    @DisplayName("Should return the forced version 3")
    void isBootMajorVersionEnabled_forced_version3() {
        SpringBootVersionResolver springBootVersionResolver = new SpringBootVersionResolver(3);
        assertFalse(springBootVersionResolver.isBootMajorVersionEnabled(2));
        assertTrue(springBootVersionResolver.isBootMajorVersionEnabled(3));
    }
}