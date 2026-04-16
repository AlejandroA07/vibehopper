package com.vibehopper.infrastructure.config;
import io.swagger.v3.oas.models.*; import io.swagger.v3.oas.models.info.Info; import io.swagger.v3.oas.models.security.*; import org.springframework.context.annotation.*;
@Configuration
public class OpenApiConfig {
    private static final String BEARER_AUTH="bearerAuth";
    @Bean OpenAPI vibeHopperOpenApi(){ return new OpenAPI().info(new Info().title("VibeHopper API").version("v1").description("Backend API contract for VibeHopper events, auth and users.")).addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH)).components(new Components().addSecuritySchemes(BEARER_AUTH,new SecurityScheme().name(BEARER_AUTH).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))); }
}
