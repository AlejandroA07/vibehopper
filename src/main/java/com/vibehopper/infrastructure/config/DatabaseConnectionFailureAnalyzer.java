package com.vibehopper.infrastructure.config;

import org.postgresql.util.PSQLException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class DatabaseConnectionFailureAnalyzer extends AbstractFailureAnalyzer<PSQLException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, PSQLException cause) {
        String state = cause.getSQLState();
        if (state == null) state = "";

        return switch (state) {
            case "08001", "08006" -> connectionRefused(cause);
            case "08004"          -> connectionRejected(cause);
            case "28000", "28P01" -> authenticationFailed(cause);
            case "3D000"          -> databaseNotFound(cause);
            default               -> messageBasedFallback(cause);
        };
    }

    private FailureAnalysis connectionRefused(PSQLException cause) {
        String address = extractAddress(cause);
        return new FailureAnalysis(
                "Cannot connect to PostgreSQL at " + address + ".\n" +
                "  The database is not accepting connections — it may not be running.",
                "Start the PostgreSQL service before launching the application.\n" +
                "  Windows  : net start postgresql-x64-17\n" +
                "  Or open  : Services → find 'postgresql' → Start\n" +
                "  Config   : spring.datasource.url in application.yml / POSTGRES_URL env var",
                cause
        );
    }

    private FailureAnalysis connectionRejected(PSQLException cause) {
        return new FailureAnalysis(
                "PostgreSQL refused the connection (pg_hba.conf rejected the client).\n" +
                "  The server is running but is not allowing this client to connect.",
                "Check pg_hba.conf and ensure the host, user and database are permitted.\n" +
                "  Reload config : SELECT pg_reload_conf();\n" +
                "  Config        : spring.datasource.url / POSTGRES_URL, POSTGRES_USER",
                cause
        );
    }

    private FailureAnalysis authenticationFailed(PSQLException cause) {
        return new FailureAnalysis(
                "PostgreSQL rejected the credentials.\n" +
                "  The server is reachable but the username or password is wrong.",
                "Verify the credentials in application.yml or environment variables.\n" +
                "  POSTGRES_USER     : current value used as the login role\n" +
                "  POSTGRES_PASSWORD : must match the role's password in PostgreSQL",
                cause
        );
    }

    private FailureAnalysis databaseNotFound(PSQLException cause) {
        return new FailureAnalysis(
                "PostgreSQL database does not exist.\n" +
                "  The server is reachable but the target database has not been created.",
                "Create the database before starting the application.\n" +
                "  psql -U postgres -c \"CREATE DATABASE vibehopperdb;\"\n" +
                "  Config : spring.datasource.url in application.yml / POSTGRES_URL env var",
                cause
        );
    }

    private FailureAnalysis messageBasedFallback(PSQLException cause) {
        String msg = cause.getMessage() == null ? "" : cause.getMessage();
        if (msg.contains("Connection to") && msg.contains("refused")) {
            return connectionRefused(cause);
        }
        return null;
    }

    private String extractAddress(PSQLException e) {
        String msg = e.getMessage();
        if (msg == null) return "unknown host";
        int start = msg.indexOf("Connection to ");
        int end   = msg.indexOf(" refused");
        if (start >= 0 && end > start) {
            return msg.substring(start + "Connection to ".length(), end);
        }
        return "unknown host";
    }
}
