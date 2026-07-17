package com.vibehopper.infrastructure.web.dto;

import com.vibehopper.domain.event.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.List;

public final class EventDtos {
  private EventDtos() {}

  public record EventRequest(
      @NotBlank String city,
      String description,
      @NotBlank String address,
      @NotBlank String name,
      List<String> keywords,
      @NotNull Category category,
      @NotNull CountryCode countryCode,
      String host,
      boolean recurring,
      @NotNull LocalDate date,
      WeekDays weekday,
      LocalTime startsAt,
      LocalTime endsAt,
      @Min(0) int capacity,
      List<String> images,
      @Valid EventSourceRequest source,
      @Valid CoordinatesRequest coordinates) {}

  public record EventResponse(
      String id,
      String city,
      String description,
      String address,
      String name,
      List<String> keywords,
      Category category,
      CountryCode countryCode,
      String host,
      String ownerId,
      LocalDate created,
      LocalDate updated,
      boolean recurring,
      LocalDate date,
      WeekDays weekday,
      LocalTime startsAt,
      LocalTime endsAt,
      int capacity,
      boolean passed,
      boolean active,
      boolean cancelled,
      List<String> images,
      List<String> attendees,
      EventSourceResponse source,
      CoordinatesResponse coordinates) {}

  public record EventSourceRequest(
      SourceType sourceType,
      String sourceName,
      String externalId,
      String sourceUrl,
      Instant lastSyncedAt) {}

  public record EventSourceResponse(
      SourceType sourceType,
      String sourceName,
      String externalId,
      String sourceUrl,
      Instant lastSyncedAt) {}

  public record CoordinatesRequest(Double latitude, Double longitude) {}

  public record CoordinatesResponse(Double latitude, Double longitude) {}
}
