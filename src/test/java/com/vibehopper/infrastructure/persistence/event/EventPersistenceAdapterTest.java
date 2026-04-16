package com.vibehopper.infrastructure.persistence.event;

import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.event.CountryCode;
import com.vibehopper.domain.event.Event;
import com.vibehopper.domain.event.EventSource;
import com.vibehopper.domain.event.SourceType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventPersistenceAdapterTest {
    @Mock private SpringDataEventRepository repository;
    @Mock private MongoTemplate mongoTemplate;

    @Test
    void saveStoresNormalizedDedupFieldsAndMapsBackToDomain() {
        EventPersistenceAdapter adapter = new EventPersistenceAdapter(repository, mongoTemplate);
        ArgumentCaptor<EventDocument> captor = ArgumentCaptor.forClass(EventDocument.class);
        when(repository.save(any(EventDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event saved = adapter.save(event(" Caf\u00E9   Meetup ", " Stockholm "));

        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getNormalizedName()).isEqualTo("cafe meetup");
        assertThat(captor.getValue().getNormalizedCity()).isEqualTo("stockholm");
        assertThat(saved.name()).isEqualTo(" Caf\u00E9   Meetup ");
    }

    @Test
    void sourceDedupDelegatesToMongoRepository() {
        EventPersistenceAdapter adapter = new EventPersistenceAdapter(repository, mongoTemplate);
        when(repository.existsBySource_SourceNameAndSource_ExternalId("meetup", "123")).thenReturn(true);

        assertThat(adapter.existsBySource("meetup", "123")).isTrue();
    }

    @Test
    void searchMapsMongoDocumentsToDomain() {
        EventPersistenceAdapter adapter = new EventPersistenceAdapter(repository, mongoTemplate);
        EventDocument document = EventPersistenceMapper.toDocument(event("Meetup", "Stockholm"), "meetup", "stockholm");
        document.setId("event-1");
        when(mongoTemplate.find(any(Query.class), eq(EventDocument.class))).thenReturn(List.of(document));

        List<Event> result = adapter.search(new EventSearchCriteria("Stockholm", Category.MEETUP, LocalDate.now(), LocalDate.now(), 59.3, 18.0, 10.0));

        assertThat(result).extracting(Event::id).containsExactly("event-1");
    }

    @Test
    void geoSearchKeepsNearSphereCriteriaAtTopLevel() {
        EventPersistenceAdapter adapter = new EventPersistenceAdapter(repository, mongoTemplate);
        ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
        when(mongoTemplate.find(any(Query.class), eq(EventDocument.class))).thenReturn(List.of());

        adapter.search(new EventSearchCriteria(null, null, null, null, 59.3293, 18.0686, 10.0));

        verify(mongoTemplate).find(captor.capture(), eq(EventDocument.class));
        assertThat(captor.getValue().getQueryObject()).containsKey("coordinates");
        assertThat(captor.getValue().getQueryObject()).doesNotContainKey("$and");
    }

    @Test
    void findByIdMapsDocumentToDomain() {
        EventPersistenceAdapter adapter = new EventPersistenceAdapter(repository, mongoTemplate);
        EventDocument document = EventPersistenceMapper.toDocument(event("Meetup", "Stockholm"), "meetup", "stockholm");
        document.setId("event-1");
        when(repository.findById("event-1")).thenReturn(Optional.of(document));

        assertThat(adapter.findById("event-1")).get().extracting(Event::id).isEqualTo("event-1");
    }

    private Event event(String name, String city) {
        return new Event(null, city, "Desc", "Central", name, List.of("tech"), Category.MEETUP, CountryCode.SE,
                "Host", "user-1", LocalDate.now(), LocalDate.now(), false, LocalDate.of(2026, 4, 16), null, null,
                null, 20, false, true, false, List.of(), List.of(),
                new EventSource(SourceType.API, "meetup", "123", "https://example.com", Instant.now()), null);
    }
}
