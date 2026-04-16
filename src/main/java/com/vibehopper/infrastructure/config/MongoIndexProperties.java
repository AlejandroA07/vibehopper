package com.vibehopper.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vibehopper.mongodb")
public record MongoIndexProperties(boolean ensureIndexes) {}
