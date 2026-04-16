package com.vibehopper.infrastructure.persistence.event;
import org.springframework.data.mongodb.repository.MongoRepository; import java.time.LocalDate;
public interface SpringDataEventRepository extends MongoRepository<EventDocument,String> { boolean existsBySource_SourceNameAndSource_ExternalId(String sourceName,String externalId); boolean existsByNormalizedNameAndDateAndNormalizedCity(String normalizedName, LocalDate date, String normalizedCity); }
