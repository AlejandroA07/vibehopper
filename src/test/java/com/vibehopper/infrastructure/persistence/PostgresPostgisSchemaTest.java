package com.vibehopper.infrastructure.persistence;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
class PostgresPostgisSchemaTest {
    private static final DockerImageName POSTGIS_IMAGE = DockerImageName
            .parse("postgis/postgis:16-3.4")
            .asCompatibleSubstituteFor("postgres");

    @Container
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGIS_IMAGE)
            .withDatabaseName("vibehopperdb")
            .withUsername("vibehopper")
            .withPassword("vibehopper");

    @Test
    void flywayCreatesPostgisSchemaIndexesAndJsonbSearchSupport() throws Exception {
        Flyway.configure()
                .dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();

        try (Connection connection = DriverManager.getConnection(
                POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword())) {
            assertThat(tableExists(connection, "app_users")).isTrue();
            assertThat(tableExists(connection, "events")).isTrue();
            assertThat(tableExists(connection, "event_attendees")).isTrue();

            Map<String, String> indexes = indexes(connection);
            assertThat(indexes).containsKeys(
                    "ux_app_users_email_lower",
                    "idx_events_normalized_city",
                    "idx_events_category",
                    "idx_events_date",
                    "ux_events_source_identity",
                    "ux_events_normalized_identity",
                    "idx_events_location_gist",
                    "idx_events_keywords_gin");
            assertThat(indexes.get("idx_events_location_gist")).contains("gist");
            assertThat(indexes.get("idx_events_keywords_gin")).contains("gin");
            assertThat(indexes.get("idx_events_keywords_gin")).contains("jsonb_path_ops");

            insertEvent(connection, "inside", 59.3293, 18.0686, "[\"tech\"]");
            insertEvent(connection, "outside", 57.7089, 11.9746, "[\"music\"]");

            assertThat(count(connection, """
                    select count(*) from events
                    where ST_DWithin(location, ST_SetSRID(ST_MakePoint(18.0686, 59.3293), 4326)::geography, 10000)
                    """)).isEqualTo(1);
            assertThat(count(connection, "select count(*) from events where keywords @> '[\"tech\"]'::jsonb"))
                    .isEqualTo(1);
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (ResultSet result = connection.getMetaData().getTables(null, "public", tableName, new String[] {"TABLE"})) {
            return result.next();
        }
    }

    private Map<String, String> indexes(Connection connection) throws SQLException {
        Map<String, String> indexes = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("select indexname, indexdef from pg_indexes where schemaname = 'public'")) {
            while (result.next()) {
                indexes.put(result.getString("indexname"), result.getString("indexdef"));
            }
        }
        return indexes;
    }

    private void insertEvent(Connection connection, String name, double latitude, double longitude, String keywords) throws SQLException {
        String sql = """
                insert into events (id, city, normalized_city, address, name, normalized_name, keywords, category,
                    country_code, date, location)
                values ('%s', 'Stockholm', 'stockholm', 'Central', '%s', '%s', '%s'::jsonb, 'MEETUP', 'SE',
                    '2026-04-16', ST_SetSRID(ST_MakePoint(%s, %s), 4326)::geography)
                """.formatted(UUID.randomUUID(), name, name, keywords, longitude, latitude);
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    private int count(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getInt(1);
        }
    }
}
