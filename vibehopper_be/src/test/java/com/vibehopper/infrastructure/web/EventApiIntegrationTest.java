package com.vibehopper.infrastructure.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.event.CountryCode;
import com.vibehopper.domain.event.Event;
import com.vibehopper.domain.event.EventSource;
import com.vibehopper.infrastructure.persistence.event.EventPersistenceAdapter;
import com.vibehopper.support.PostgresContainerSupport;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EventApiIntegrationTest extends PostgresContainerSupport {
  @Autowired private MockMvc mockMvc;

  @Autowired private EventPersistenceAdapter events;

  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void cleanDatabase() {
    jdbcTemplate.execute("TRUNCATE TABLE event_attendees, events, app_users CASCADE");
  }

  @Test
  void anonymousEventEndpointsReturnPagedPublicDtosFromRealPostgres() throws Exception {
    Event stockholm = events.save(event("Stockholm Concert", "Stockholm"));
    events.save(event("Gothenburg Concert", "Gothenburg"));

    mockMvc
        .perform(
            get("/api/events").param("city", "Stockholm").param("page", "0").param("size", "1"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.items.length()").value(1))
        .andExpect(jsonPath("$.items[0].id").value(stockholm.id()))
        .andExpect(jsonPath("$.items[0].name").value("Stockholm Concert"))
        .andExpect(jsonPath("$.items[0].ownerId").doesNotExist())
        .andExpect(jsonPath("$.items[0].attendees").doesNotExist())
        .andExpect(jsonPath("$.page").value(0))
        .andExpect(jsonPath("$.size").value(1))
        .andExpect(jsonPath("$.totalItems").value(1))
        .andExpect(jsonPath("$.totalPages").value(1));

    mockMvc
        .perform(get("/api/events/{id}", stockholm.id()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(stockholm.id()))
        .andExpect(jsonPath("$.attendeeCount").value(0))
        .andExpect(jsonPath("$.ownerId").doesNotExist())
        .andExpect(jsonPath("$.attendees").doesNotExist())
        .andExpect(jsonPath("$.externalId").doesNotExist())
        .andExpect(jsonPath("$.lastSyncedAt").doesNotExist());
  }

  private Event event(String name, String city) {
    return Event.builder()
        .name(name)
        .description("A public event")
        .city(city)
        .address("Central Station")
        .keywords(List.of("music"))
        .category(Category.MUSIC)
        .countryCode(CountryCode.SE)
        .host("VibeHopper")
        .created(LocalDate.of(2026, 7, 1))
        .updated(LocalDate.of(2026, 7, 1))
        .date(LocalDate.of(2026, 8, 1))
        .startsAt(LocalTime.of(19, 0))
        .endsAt(LocalTime.of(22, 0))
        .capacity(100)
        .active(true)
        .attendees(List.of())
        .source(EventSource.manual())
        .build();
  }
}
