package com.vibehopper.infrastructure.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {
  public static final String BEARER_AUTH = "bearerAuth";
  public static final String CSRF_TOKEN = "csrfToken";

  @Bean
  OpenAPI vibehopperOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("VibeHopper API")
                .version("v1")
                .description(
                    "Backend API contract for VibeHopper events, auth and users. Unsafe requests also require the cookie/header pair issued by GET /api/csrf."))
        .components(
            new Components()
                .addSecuritySchemes(
                    BEARER_AUTH,
                    new SecurityScheme()
                        .name(BEARER_AUTH)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                .addSecuritySchemes(
                    CSRF_TOKEN,
                    new SecurityScheme()
                        .name("X-XSRF-TOKEN")
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)));
  }
}
