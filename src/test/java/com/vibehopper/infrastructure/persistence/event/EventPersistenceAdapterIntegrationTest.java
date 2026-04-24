package com.vibehopper.infrastructure.persistence.event;

import com.vibehopper.application.common.ConflictException;
import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.event.Coordinates;
import com.vibehopper.domain.event.CountryCode;
import com.vibehopper.domain.event.Event;
import com.vibehopper.domain.event.EventSource;
import com.vibehopper.domain.event.SourceType;
import com.vibehopper.support.PostgresContainerSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class EventPersistenceAdapterIntegrationTest extends PostgresContainerSupport {
    @Autowired
    private EventPersistenceAdapter adapter;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE event_attendees, events, app_users CASCADE");
    }

    @Test
    void savePersistsNormalizedFieldsJsonbAndCoordinatesInRealPostgres() {
        Event saved = adapter.save(event(" Caf\u00E9   Meetup ", " Stockholm ", "source-1", "ext-1",
                new Coordinates(59.3293, 18.0686), List.of("tech", "meetup"), List.of("cover.jpg")));

        Map<String, Object> row = jdbcTemplate.queryForMap("""
                select normalized_name,
                       normalized_city,
                       keywords::text as keywords,
                       images::text as images,
                       ST_X(location::geometry) as longitude,
                       ST_Y(location::geometry) as latitude
                from events
                where id = ?::uuid
                """, saved.id());

        assertThat(row.get("normalized_name")).isEqualTo("cafe meetup");
        assertThat(row.get("normalized_city")).isEqualTo("stockholm");
        assertThat(String.valueOf(row.get("keywords"))).contains("tech", "meetup");
        assertThat(String.valueOf(row.get("images"))).contains("cover.jpg");
        assertThat(((Number) row.get("longitude")).doubleValue()).isEqualTo(18.0686);
        assertThat(((Number) row.get("latitude")).doubleValue()).isEqualTo(59.3293);
        assertThat(adapter.findById(saved.id())).isPresent();
    }

    @Test
    void duplicateSourceIdentityConflictComesFromDatabaseUniqueIndex() {
        adapter.save(event("Meetup One", "Stockholm", "meetup", "123",
                new Coordinates(59.3293, 18.0686), List.of("tech"), List.of()));

        assertThatThrownBy(() -> adapter.save(event("Meetup Two", "Stockholm", "meetup", "123",
                new Coordinates(59.3294, 18.0687), List.of("music"), List.of())))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Event already exists");
    }

    @Test
    void duplicateNormalizedIdentityConflictComesFromDatabaseUniqueIndex() {
        adapter.save(event(" Caf\u00E9   Meetup ", " Stockholm ", null, null,
                new Coordinates(59.3293, 18.0686), List.of("tech"), List.of()));

        assertThatThrownBy(() -> adapter.save(event("Cafe Meetup", "stockholm", "other-source", "456",
                new Coordinates(59.3294, 18.0687), List.of("music"), List.of())))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Event already exists");
    }

    @Test
    void geoSearchReturnsNearbyEventsAndExcludesFarOnes() {
        Event nearby = adapter.save(event("Nearby", "Stockholm", "source-1", "nearby",
                new Coordinates(59.3293, 18.0686), List.of("tech"), List.of()));
        adapter.save(event("Far Away", "Gothenburg", "source-2", "far",
                new Coordinates(57.7089, 11.9746), List.of("music"), List.of()));

        List<Event> result = adapter.search(new EventSearchCriteria(
                null, null, null, null, 59.3293, 18.0686, 10.0));

        assertThat(result).extracting(Event::id).containsExactly(nearby.id());
    }

    private Event event(
            String name,
            String city,
            String sourceName,
            String externalId,
            Coordinates coordinates,
            List<String> keywords,
            List<String> images
    ) {
        EventSource source = sourceName == null && externalId == null
                ? null
                : new EventSource(
                        SourceType.API,
                        sourceName,
                        externalId,
                        "https://example.com/" + (externalId == null ? "event" : externalId),
                        Instant.parse("2026-04-16T10:15:30Z"));

        return new Event(
                null,
                city,
                "Integration test event",
                "Central Station",
                name,
                keywords,
                Category.MEETUP,
                CountryCode.SE,
                "VibeHopper",
                null,
                LocalDate.of(2026, 4, 1),
                LocalDate.of(2026, 4, 2),
                false,
                LocalDate.of(2026, 4, 16),
                null,
                null,
                null,
                30,
                false,
                true,
                false,
                images,
                List.of(),
                source,
                coordinates);
    }
}
