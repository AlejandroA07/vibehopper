package com.vibehopper.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class SecurityConfigurationStaticTest {
  @Test
  void securityRulesKeepPublicReadAuthAndAdminBoundaries() throws IOException {
    String source =
        Files.readString(
            Path.of(
                "src/main/java/com/vibehopper/infrastructure/security/SecurityConfiguration.java"));
    String compactSource = source.replaceAll("\\s+", "");

    assertThat(source).contains("/api/auth/register", "/api/auth/login");
    assertThat(source).contains("/error").contains("permitAll()");
    assertThat(compactSource).contains("HttpMethod.GET,\"/api/events\",\"/api/events/**\"");
    assertThat(source).contains("/api/admin/**").contains("hasRole(\"ADMIN\")");
    assertThat(compactSource)
        .contains("HttpMethod.POST,\"/api/events\"")
        .contains("hasAnyRole(\"USER\",\"ADMIN\")");
    assertThat(source).contains("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**");
  }
}
