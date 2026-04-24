package com.vibehopper.infrastructure.persistence.event;

import com.vibehopper.application.common.ConflictException;
import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.application.event.port.EventRepositoryPort;
import com.vibehopper.domain.event.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EventPersistenceAdapter implements EventRepositoryPort {
    private final EventJpaRepository repository;
    private final EntityManager entityManager;

    public EventPersistenceAdapter(EventJpaRepository repository, EntityManager entityManager) {
        this.repository = repository;
        this.entityManager = entityManager;
    }

    @Override
    public Event save(Event event) {
        try {
            EventEntity entity = EventPersistenceMapper.toEntity(event, normalize(event.name()), normalize(event.city()));
            return EventPersistenceMapper.toDomain(repository.saveAndFlush(entity));
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Event already exists");
        }
    }

    @Override
    public List<Event> findAll() {
        return repository.findAll().stream().map(EventPersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<Event> findById(String id) {
        return toUuid(id).flatMap(repository::findById).map(EventPersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        toUuid(id).ifPresent(repository::deleteById);
    }

    @Override
    public List<Event> search(EventSearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("select e.* from events e where 1 = 1");
        Map<String, Object> parameters = new LinkedHashMap<>();

        if (hasText(criteria.city())) {
            sql.append(" and e.normalized_city = :city");
            parameters.put("city", normalize(criteria.city()));
        }
        if (criteria.category() != null) {
            sql.append(" and e.category = :category");
            parameters.put("category", criteria.category().name());
        }
        if (criteria.from() != null) {
            sql.append(" and e.date >= :fromDate");
            parameters.put("fromDate", criteria.from());
        }
        if (criteria.to() != null) {
            sql.append(" and e.date <= :toDate");
            parameters.put("toDate", criteria.to());
        }
        if (criteria.hasGeoSearch()) {
            sql.append(" and e.location is not null");
            sql.append(" and ST_DWithin(e.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, :radiusMeters)");
            sql.append(" order by ST_Distance(e.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography) asc");
            parameters.put("lng", criteria.longitude());
            parameters.put("lat", criteria.latitude());
            parameters.put("radiusMeters", criteria.radiusKm() * 1000.0);
        }

        Query query = entityManager.createNativeQuery(sql.toString(), EventEntity.class);
        parameters.forEach(query::setParameter);

        @SuppressWarnings("unchecked")
        List<EventEntity> entities = query.getResultList();
        return entities.stream().map(EventPersistenceMapper::toDomain).toList();
    }

    @Override
    public boolean existsBySource(String sourceName, String externalId) {
        return repository.existsBySourceNameAndExternalId(sourceName, externalId);
    }

    @Override
    public boolean existsByNormalizedIdentity(String normalizedName, LocalDate date, String city) {
        return repository.existsByNormalizedNameAndDateAndNormalizedCity(normalizedName, date, city);
    }

    private Optional<UUID> toUuid(String id) {
        try {
            return Optional.of(UUID.fromString(id));
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .trim()
                .replaceAll("\\s+", " ");
        return normalized.isBlank() ? null : normalized;
    }
}
