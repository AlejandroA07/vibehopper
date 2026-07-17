package com.vibehopper.infrastructure.persistence.event;

import java.time.LocalDate;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventJpaRepository extends JpaRepository<EventEntity, UUID> {
  boolean existsBySourceNameAndExternalId(String sourceName, String externalId);

  boolean existsByNormalizedNameAndDateAndNormalizedCity(
      String normalizedName, LocalDate date, String normalizedCity);
}
