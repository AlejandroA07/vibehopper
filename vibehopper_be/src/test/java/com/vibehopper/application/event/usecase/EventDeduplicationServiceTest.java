package com.vibehopper.application.event.usecase;

import static org.assertj.core.api.Assertions.assertThat;

import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.application.event.port.EventRepositoryPort;
import com.vibehopper.application.event.query.EventPage;
import com.vibehopper.domain.event.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;

class EventDeduplicationServiceTest {
  @Test
  void sourceNameAndExternalIdTakePrecedenceForDuplicates() {
    EventDeduplicationService service =
        new EventDeduplicationService(new FakeRepository(true, false));
    Event event =
        eventWithSource(
            new EventSource(
                SourceType.API, "meetup", "abc-123", "https://example.com", Instant.now()));
    assertThat(service.isDuplicate(event)).isTrue();
  }

  @Test
  void fallbackUsesNormalizedNameDateAndCity() {
    EventDeduplicationService service =
        new EventDeduplicationService(new FakeRepository(false, true));
    assertThat(service.isDuplicate(eventWithSource(null))).isTrue();
  }

  @Test
  void normalizeLowercasesTrimsWhitespaceAndRemovesAccents() {
    EventDeduplicationService service =
        new EventDeduplicationService(new FakeRepository(false, false));
    assertThat(service.normalize("  Café   Meetup  ")).isEqualTo("cafe meetup");
  }

  private Event eventWithSource(EventSource source) {
    return Event.builder()
        .city("Stockholm")
        .address("Central")
        .name("Café Meetup")
        .keywords(List.of())
        .category(Category.MEETUP)
        .countryCode(CountryCode.SE)
        .date(LocalDate.of(2026, 4, 16))
        .source(source)
        .build();
  }

  private record FakeRepository(boolean sourceDuplicate, boolean fallbackDuplicate)
      implements EventRepositoryPort {
    public Event save(Event e) {
      return e;
    }

    public Optional<Event> findById(String id) {
      return Optional.empty();
    }

    public void deleteById(String id) {}

    public EventPage search(EventSearchCriteria c) {
      return new EventPage(List.of(), c.page(), c.size(), 0);
    }

    public boolean existsBySource(String s, String e) {
      return sourceDuplicate;
    }

    public boolean existsByNormalizedIdentity(String n, LocalDate d, String c) {
      return fallbackDuplicate;
    }
  }
}
