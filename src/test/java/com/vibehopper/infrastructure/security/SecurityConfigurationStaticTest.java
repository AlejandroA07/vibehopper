package com.vibehopper.infrastructure.security;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigurationStaticTest {
    @Test
    void securityRulesKeepPublicReadAuthAndAdminBoundaries() throws IOException {
        String source = Files.readString(Path.of("src/main/java/com/vibehopper/infrastructure/security/SecurityConfiguration.java"));

        assertThat(source).contains("/api/auth/register", "/api/auth/login");
        assertThat(source).contains("/error").contains("permitAll()");
        assertThat(source).contains("HttpMethod.GET,\"/api/events\",\"/api/events/**\"");
        assertThat(source).contains("/api/admin/**").contains("hasRole(\"ADMIN\")");
        assertThat(source).contains("HttpMethod.POST,\"/api/events\"").contains("hasAnyRole(\"USER\",\"ADMIN\")");
        assertThat(source).contains("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**");
    }
}
