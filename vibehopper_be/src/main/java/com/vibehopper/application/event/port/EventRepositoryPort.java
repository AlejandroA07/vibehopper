package com.vibehopper.application.event.port;

import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.domain.event.Event;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepositoryPort {
  Event save(Event event);

  List<Event> findAll();

  Optional<Event> findById(String id);

  void deleteById(String id);

  List<Event> search(EventSearchCriteria criteria);

  boolean existsBySource(String sourceName, String externalId);

  boolean existsByNormalizedIdentity(String normalizedName, LocalDate date, String city);
}
