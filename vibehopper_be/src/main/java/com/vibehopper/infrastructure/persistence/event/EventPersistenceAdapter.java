package com.vibehopper.infrastructure.persistence.event;

import com.vibehopper.application.common.ConflictException;
import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.application.event.port.EventRepositoryPort;
import com.vibehopper.application.event.query.EventPage;
import com.vibehopper.domain.event.Event;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

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
      EventEntity entity =
          EventPersistenceMapper.toEntity(event, normalize(event.name()), normalize(event.city()));
      return EventPersistenceMapper.toDomain(repository.saveAndFlush(entity));
    } catch (DataIntegrityViolationException exception) {
      throw new ConflictException("Event already exists");
    }
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
  public EventPage search(EventSearchCriteria criteria) {
    StringBuilder where = new StringBuilder(" from events e where 1 = 1");
    Map<String, Object> parameters = new LinkedHashMap<>();

    if (hasText(criteria.city())) {
      where.append(" and e.normalized_city = :city");
      parameters.put("city", normalize(criteria.city()));
    }
    if (criteria.category() != null) {
      where.append(" and e.category = :category");
      parameters.put("category", criteria.category().name());
    }
    if (criteria.from() != null) {
      where.append(" and e.date >= :fromDate");
      parameters.put("fromDate", criteria.from());
    }
    if (criteria.to() != null) {
      where.append(" and e.date <= :toDate");
      parameters.put("toDate", criteria.to());
    }
    if (criteria.hasGeoSearch()) {
      where.append(" and e.location is not null");
      where.append(
          " and ST_DWithin(e.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, :radiusMeters)");
      parameters.put("lng", criteria.longitude());
      parameters.put("lat", criteria.latitude());
      parameters.put("radiusMeters", criteria.radiusKm() * 1000.0);
    }

    Query countQuery = entityManager.createNativeQuery("select count(*)" + where);
    applyParameters(countQuery, parameters);
    long totalItems = ((Number) countQuery.getSingleResult()).longValue();

    StringBuilder select = new StringBuilder("select e.*").append(where);
    if (criteria.hasGeoSearch()) {
      select.append(
          " order by ST_Distance(e.location, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography) asc,");
    } else {
      select.append(" order by");
    }
    select.append(" e.date asc, e.starts_at asc nulls last, e.id asc");

    Query query = entityManager.createNativeQuery(select.toString(), EventEntity.class);
    applyParameters(query, parameters);
    query.setFirstResult(criteria.page() * criteria.size());
    query.setMaxResults(criteria.size());

    @SuppressWarnings("unchecked")
    List<EventEntity> entities = query.getResultList();
    List<Event> items = entities.stream().map(EventPersistenceMapper::toDomain).toList();
    return new EventPage(items, criteria.page(), criteria.size(), totalItems);
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

  private void applyParameters(Query query, Map<String, Object> parameters) {
    parameters.forEach(query::setParameter);
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private String normalize(String value) {
    if (value == null) {
      return null;
    }
    String normalized =
        Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT)
            .trim()
            .replaceAll("\\s+", " ");
    return normalized.isBlank() ? null : normalized;
  }
}
