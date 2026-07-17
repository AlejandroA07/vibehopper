package com.vibehopper.application.event.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vibehopper.application.common.ForbiddenException;
import com.vibehopper.application.common.NotFoundException;
import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.application.event.port.EventRepositoryPort;
import com.vibehopper.application.event.query.EventPage;
import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.event.CountryCode;
import com.vibehopper.domain.event.Event;
import com.vibehopper.domain.event.EventSource;
import com.vibehopper.domain.event.SourceType;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EventUseCaseTest {
  @Test
  void updateFullyReplacesEditableFieldsAndPreservesServerControlledFields() {
    FakeRepository repository = new FakeRepository(existingEvent());
    EventUseCase useCase = new EventUseCase(repository, new EventDeduplicationService(repository));
    Event request = requestEvent("New name").toBuilder().capacity(0).host(null).build();

    Event updated = useCase.update("event-1", request, "owner-1", false);

    assertThat(updated.id()).isEqualTo("event-1");
    assertThat(updated.name()).isEqualTo("New name");
    assertThat(updated.capacity()).isZero();
    assertThat(updated.host()).isNull();
    assertThat(updated.ownerId()).isEqualTo("owner-1");
    assertThat(updated.attendees()).containsExactly("attendee-1");
    assertThat(updated.source().externalId()).isEqualTo("external-1");
    assertThat(updated.active()).isTrue();
  }

  @Test
  void deleteFailsWhenEventDoesNotExist() {
    FakeRepository repository = new FakeRepository(null);
    EventUseCase useCase = new EventUseCase(repository, new EventDeduplicationService(repository));

    assertThatThrownBy(() -> useCase.deleteById("missing", "owner-1", false))
        .isInstanceOf(NotFoundException.class);
  }

  @Test
  void userCannotUpdateAnotherUsersEvent() {
    FakeRepository repository = new FakeRepository(existingEvent());
    EventUseCase useCase = new EventUseCase(repository, new EventDeduplicationService(repository));

    assertThatThrownBy(() -> useCase.update("event-1", requestEvent("New name"), "user-2", false))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void userCannotDeleteAnotherUsersEvent() {
    FakeRepository repository = new FakeRepository(existingEvent());
    EventUseCase useCase = new EventUseCase(repository, new EventDeduplicationService(repository));

    assertThatThrownBy(() -> useCase.deleteById("event-1", "user-2", false))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void adminCanUpdateAndDeleteAnyEvent() {
    FakeRepository repository = new FakeRepository(existingEvent());
    EventUseCase useCase = new EventUseCase(repository, new EventDeduplicationService(repository));

    Event updated = useCase.update("event-1", requestEvent("Admin update"), "admin-1", true);
    useCase.deleteById("event-1", "admin-1", true);

    assertThat(updated.name()).isEqualTo("Admin update");
    assertThat(repository.deletedIds).containsExactly("event-1");
  }

  @Test
  void geoSearchRequiresLatitudeLongitudeAndRadiusTogether() {
    EventUseCase useCase = useCase();

    assertThatThrownBy(
            () ->
                useCase.search(
                    new EventSearchCriteria(null, null, null, null, 59.3, null, 10.0, 0, 20)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("lat, lng and radiusKm must be provided together");
  }

  @Test
  void searchRejectsInvertedDateRange() {
    EventUseCase useCase = useCase();

    assertThatThrownBy(
            () ->
                useCase.search(
                    new EventSearchCriteria(
                        null,
                        null,
                        LocalDate.of(2026, 4, 17),
                        LocalDate.of(2026, 4, 16),
                        null,
                        null,
                        null,
                        0,
                        20)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("from must be on or before to");
  }

  @Test
  void searchRejectsUnboundedPageInputsFromInternalCallers() {
    EventUseCase useCase = useCase();

    assertThatThrownBy(
            () ->
                useCase.search(
                    new EventSearchCriteria(null, null, null, null, null, null, null, 0, 101)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("size must be between 1 and 100");
  }

  @Test
  void createSetsServerControlledOwnerLifecycleAttendeesAndManualSource() {
    FakeRepository repository = new FakeRepository(null);
    EventUseCase useCase = new EventUseCase(repository, new EventDeduplicationService(repository));
    Event clientEvent =
        requestEvent("Meetup").toBuilder()
            .attendees(List.of("attacker-selected-user"))
            .source(
                new EventSource(
                    SourceType.API,
                    "client-source",
                    "client-id",
                    "https://example.com",
                    Instant.now()))
            .build();

    Event created = useCase.create(clientEvent, "owner-1");

    assertThat(created.ownerId()).isEqualTo("owner-1");
    assertThat(created.active()).isTrue();
    assertThat(created.created()).isNotNull();
    assertThat(created.updated()).isNotNull();
    assertThat(created.attendees()).isEmpty();
    assertThat(created.source().sourceType()).isEqualTo(SourceType.MANUAL);
  }

  private EventUseCase useCase() {
    FakeRepository repository = new FakeRepository(existingEvent());
    return new EventUseCase(repository, new EventDeduplicationService(repository));
  }

  private Event existingEvent() {
    return Event.builder()
        .id("event-1")
        .city("Stockholm")
        .description("Old")
        .address("Central")
        .name("Old name")
        .keywords(List.of())
        .category(Category.MEETUP)
        .countryCode(CountryCode.SE)
        .host("Host")
        .ownerId("owner-1")
        .created(LocalDate.now())
        .updated(LocalDate.now())
        .date(LocalDate.now())
        .capacity(10)
        .active(true)
        .attendees(List.of("attendee-1"))
        .source(
            new EventSource(
                SourceType.API,
                "community-calendar",
                "external-1",
                "https://example.com/events/1",
                Instant.parse("2026-04-16T10:15:30Z")))
        .build();
  }

  private Event requestEvent(String name) {
    return Event.builder()
        .city("Stockholm")
        .description("Updated description")
        .address("Central")
        .name(name)
        .keywords(List.of("tech"))
        .category(Category.MEETUP)
        .countryCode(CountryCode.SE)
        .host("Updated host")
        .date(LocalDate.of(2026, 4, 16))
        .capacity(20)
        .images(List.of())
        .build();
  }

  private static final class FakeRepository implements EventRepositoryPort {
    private Event event;
    private final List<String> deletedIds = new ArrayList<>();

    private FakeRepository(Event event) {
      this.event = event;
    }

    @Override
    public Event save(Event event) {
      this.event = event;
      return event;
    }

    @Override
    public Optional<Event> findById(String id) {
      return Optional.ofNullable(event).filter(existing -> existing.id().equals(id));
    }

    @Override
    public void deleteById(String id) {
      deletedIds.add(id);
    }

    @Override
    public EventPage search(EventSearchCriteria criteria) {
      List<Event> items = event == null ? List.of() : List.of(event);
      return new EventPage(items, criteria.page(), criteria.size(), items.size());
    }

    @Override
    public boolean existsBySource(String sourceName, String externalId) {
      return false;
    }

    @Override
    public boolean existsByNormalizedIdentity(String normalizedName, LocalDate date, String city) {
      return false;
    }
  }
}
