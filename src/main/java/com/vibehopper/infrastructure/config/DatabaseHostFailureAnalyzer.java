package com.vibehopper.infrastructure.config;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

import java.net.UnknownHostException;

public class DatabaseHostFailureAnalyzer extends AbstractFailureAnalyzer<UnknownHostException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, UnknownHostException cause) {
        return new FailureAnalysis(
                "Cannot resolve the PostgreSQL hostname: " + cause.getMessage() + ".\n" +
                "  The hostname in the datasource URL does not exist or cannot be reached.",
                "Check the hostname in your datasource configuration.\n" +
                "  Config : spring.datasource.url in application.yml / POSTGRES_URL env var\n" +
                "  For local development the value should be: localhost",
                cause
        );
    }
}
