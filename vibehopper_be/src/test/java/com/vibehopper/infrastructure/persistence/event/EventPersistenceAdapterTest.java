package com.vibehopper.infrastructure.persistence.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.event.Coordinates;
import com.vibehopper.domain.event.CountryCode;
import com.vibehopper.domain.event.Event;
import com.vibehopper.domain.event.EventSource;
import com.vibehopper.domain.event.SourceType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class EventPersistenceAdapterTest {
  @Mock private EventJpaRepository repository;
  @Mock private EntityManager entityManager;
  @Mock private Query query;

  @Test
  void saveStoresNormalizedDedupFieldsAndMapsBackToDomain() {
    EventPersistenceAdapter adapter = new EventPersistenceAdapter(repository, entityManager);
    ArgumentCaptor<EventEntity> captor = ArgumentCaptor.forClass(EventEntity.class);
    when(repository.saveAndFlush(any(EventEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Event saved = adapter.save(event(" Cafe   Meetup ", " Stockholm "));

    verify(repository).saveAndFlush(captor.capture());
    assertThat(captor.getValue().getNormalizedName()).isEqualTo("cafe meetup");
    assertThat(captor.getValue().getNormalizedCity()).isEqualTo("stockholm");
    assertThat(saved.name()).isEqualTo(" Cafe   Meetup ");
  }

  @Test
  void coordinatesMapLatitudeAndLongitudeWithoutSwappingDomainSemantics() {
    EventEntity entity =
        EventPersistenceMapper.toEntity(event("Meetup", "Stockholm"), "meetup", "stockholm");

    Point location = entity.getLocation();
    Coordinates coordinates = EventPersistenceMapper.toDomainCoordinates(location);

    assertThat(location.getX()).isEqualTo(18.0686);
    assertThat(location.getY()).isEqualTo(59.3293);
    assertThat(location.getSRID()).isEqualTo(4326);
    assertThat(coordinates.latitude()).isEqualTo(59.3293);
    assertThat(coordinates.longitude()).isEqualTo(18.0686);
  }

  @Test
  void sourceDedupDelegatesToJpaRepository() {
    EventPersistenceAdapter adapter = new EventPersistenceAdapter(repository, entityManager);
    when(repository.existsBySourceNameAndExternalId("meetup", "123")).thenReturn(true);

    assertThat(adapter.existsBySource("meetup", "123")).isTrue();
  }

  @Test
  void searchUsesBoundPostgisNativeQuery() {
    EventPersistenceAdapter adapter = new EventPersistenceAdapter(repository, entityManager);
    EventEntity entity =
        EventPersistenceMapper.toEntity(event("Meetup", "Stockholm"), "meetup", "stockholm");
    ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
    when(entityManager.createNativeQuery(sqlCaptor.capture(), eq(EventEntity.class)))
        .thenReturn(query);
    when(query.setParameter(anyString(), any())).thenReturn(query);
    when(query.getResultList()).thenReturn(List.of(entity));

    List<Event> result =
        adapter.search(
            new EventSearchCriteria(
                "Stockholm",
                Category.MEETUP,
                LocalDate.of(2026, 4, 16),
                LocalDate.of(2026, 4, 17),
                59.3,
                18.0,
                10.0));

    assertThat(result).extracting(Event::name).containsExactly("Meetup");
    assertThat(sqlCaptor.getValue())
        .contains("ST_DWithin", "ST_Distance", ":lng", ":lat", ":radiusMeters");
    verify(query).setParameter("city", "stockholm");
    verify(query).setParameter("category", "MEETUP");
    verify(query).setParameter("radiusMeters", 10_000.0);
  }

  @Test
  void duplicateConstraintMapsToConflictException() {
    EventPersistenceAdapter adapter = new EventPersistenceAdapter(repository, entityManager);
    when(repository.saveAndFlush(any(EventEntity.class)))
        .thenThrow(new DataIntegrityViolationException("duplicate"));

    assertThatThrownBy(() -> adapter.save(event("Meetup", "Stockholm")))
        .isInstanceOf(com.vibehopper.application.common.ConflictException.class)
        .hasMessage("Event already exists");
  }

  @Test
  void findByIdMapsEntityToDomain() {
    EventPersistenceAdapter adapter = new EventPersistenceAdapter(repository, entityManager);
    UUID id = UUID.randomUUID();
    EventEntity entity =
        EventPersistenceMapper.toEntity(event("Meetup", "Stockholm"), "meetup", "stockholm");
    entity.setId(id);
    when(repository.findById(id)).thenReturn(Optional.of(entity));

    assertThat(adapter.findById(id.toString()))
        .get()
        .extracting(Event::id)
        .isEqualTo(id.toString());
  }

  private Event event(String name, String city) {
    return Event.builder()
        .city(city)
        .description("Desc")
        .address("Central")
        .name(name)
        .keywords(List.of("tech"))
        .category(Category.MEETUP)
        .countryCode(CountryCode.SE)
        .host("Host")
        .created(LocalDate.now())
        .updated(LocalDate.now())
        .date(LocalDate.of(2026, 4, 16))
        .capacity(20)
        .active(true)
        .source(
            new EventSource(SourceType.API, "meetup", "123", "https://example.com", Instant.now()))
        .coordinates(new Coordinates(59.3293, 18.0686))
        .build();
  }
}
