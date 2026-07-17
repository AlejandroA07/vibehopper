package com.vibehopper.infrastructure.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vibehopper.cors")
public record CorsProperties(List<String> allowedOrigins) {}
