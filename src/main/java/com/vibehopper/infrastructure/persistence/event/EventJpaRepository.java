package com.vibehopper.infrastructure.persistence.event;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.UUID;

public interface EventJpaRepository extends JpaRepository<EventEntity, UUID> {
    boolean existsBySourceNameAndExternalId(String sourceName, String externalId);
    boolean existsByNormalizedNameAndDateAndNormalizedCity(String normalizedName, LocalDate date, String normalizedCity);
}
