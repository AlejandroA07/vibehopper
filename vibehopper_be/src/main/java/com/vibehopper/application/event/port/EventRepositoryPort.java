package com.vibehopper.application.event.port;

import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.application.event.query.EventPage;
import com.vibehopper.domain.event.Event;
import java.time.LocalDate;
import java.util.Optional;

public interface EventRepositoryPort {
  Event save(Event event);

  Optional<Event> findById(String id);

  void deleteById(String id);

  EventPage search(EventSearchCriteria criteria);

  boolean existsBySource(String sourceName, String externalId);

  boolean existsByNormalizedIdentity(String normalizedName, LocalDate date, String city);
}
