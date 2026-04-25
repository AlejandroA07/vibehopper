package com.vibehopper.application.event.usecase;

import com.vibehopper.application.common.NotFoundException;
import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.application.event.port.EventRepositoryPort;
import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.event.CountryCode;
import com.vibehopper.domain.event.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventUseCaseTest {
    @Test
    void updateUsesPathIdAndKeepsExistingOwner() {
        FakeRepository repository = new FakeRepository(existingEvent());
        EventUseCase useCase = new EventUseCase(repository, new EventDeduplicationService(repository));

        Event updated = useCase.update("event-1", requestEvent("New name"));

        assertThat(updated.id()).isEqualTo("event-1");
        assertThat(updated.name()).isEqualTo("New name");
        assertThat(updated.ownerId()).isEqualTo("owner-1");
    }

    @Test
    void deleteFailsWhenEventDoesNotExist() {
        FakeRepository repository = new FakeRepository(null);
        EventUseCase useCase = new EventUseCase(repository, new EventDeduplicationService(repository));

        assertThatThrownBy(() -> useCase.deleteById("missing")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void geoSearchRequiresLatitudeLongitudeAndRadiusTogether() {
        FakeRepository repository = new FakeRepository(existingEvent());
        EventUseCase useCase = new EventUseCase(repository, new EventDeduplicationService(repository));

        assertThatThrownBy(() -> useCase.search(new EventSearchCriteria(null, null, null, null, 59.3, null, 10.0)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void createSetsOwnerLifecycleDefaultsAndManualSourceFallback() {
        FakeRepository repository = new FakeRepository(null);
        EventUseCase useCase = new EventUseCase(repository, new EventDeduplicationService(repository));

        Event created = useCase.create(requestEvent("Meetup"), "owner-1");

        assertThat(created.ownerId()).isEqualTo("owner-1");
        assertThat(created.active()).isTrue();
        assertThat(created.created()).isNotNull();
        assertThat(created.updated()).isNotNull();
        assertThat(created.source().sourceType()).isEqualTo(com.vibehopper.domain.event.SourceType.MANUAL);
    }

    private Event existingEvent() {
        return Event.builder()
                .id("event-1").city("Stockholm").description("Old").address("Central").name("Old name")
                .keywords(List.of()).category(Category.MEETUP).countryCode(CountryCode.SE).host("Host")
                .ownerId("owner-1").created(LocalDate.now()).updated(LocalDate.now())
                .date(LocalDate.now()).capacity(10).active(true)
                .build();
    }

    private Event requestEvent(String name) {
        return Event.builder().name(name).build();
    }

    private static final class FakeRepository implements EventRepositoryPort {
        private Event event;
        private final List<String> deletedIds = new ArrayList<>();

        private FakeRepository(Event event) {
            this.event = event;
        }

        @Override public Event save(Event event) { this.event = event; return event; }
        @Override public List<Event> findAll() { return event == null ? List.of() : List.of(event); }
        @Override public Optional<Event> findById(String id) { return Optional.ofNullable(event).filter(e -> e.id().equals(id)); }
        @Override public void deleteById(String id) { deletedIds.add(id); }
        @Override public List<Event> search(EventSearchCriteria criteria) { return List.of(); }
        @Override public boolean existsBySource(String sourceName, String externalId) { return false; }
        @Override public boolean existsByNormalizedIdentity(String normalizedName, LocalDate date, String city) { return false; }
    }
}
