package com.vibehopper.infrastructure.web.dto;

import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.event.CountryCode;
import com.vibehopper.domain.event.WeekDays;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class EventDtos {
  private EventDtos() {}

  public record CreateEventRequest(
      @NotBlank @Size(max = 120) String city,
      @Size(max = 5000) String description,
      @NotBlank @Size(max = 300) String address,
      @NotBlank @Size(max = 200) String name,
      @Size(max = 20) List<@NotBlank @Size(max = 64) String> keywords,
      @NotNull Category category,
      @NotNull CountryCode countryCode,
      @Size(max = 200) String host,
      boolean recurring,
      @NotNull LocalDate date,
      WeekDays weekday,
      LocalTime startsAt,
      LocalTime endsAt,
      @Min(0) @Max(100_000) int capacity,
      @Size(max = 10) List<@NotBlank @Size(max = 2048) String> images,
      @Valid CoordinatesRequest coordinates) {}

  public record UpdateEventRequest(
      @NotBlank @Size(max = 120) String city,
      @Size(max = 5000) String description,
      @NotBlank @Size(max = 300) String address,
      @NotBlank @Size(max = 200) String name,
      @Size(max = 20) List<@NotBlank @Size(max = 64) String> keywords,
      @NotNull Category category,
      @NotNull CountryCode countryCode,
      @Size(max = 200) String host,
      boolean recurring,
      @NotNull LocalDate date,
      WeekDays weekday,
      LocalTime startsAt,
      LocalTime endsAt,
      @Min(0) @Max(100_000) int capacity,
      @Size(max = 10) List<@NotBlank @Size(max = 2048) String> images,
      @Valid CoordinatesRequest coordinates) {}

  public record EventPageResponse(
      List<EventSummaryResponse> items, int page, int size, long totalItems, int totalPages) {}

  public record EventSummaryResponse(
      String id,
      String name,
      String city,
      String address,
      Category category,
      CountryCode countryCode,
      String host,
      boolean recurring,
      LocalDate date,
      LocalTime startsAt,
      LocalTime endsAt,
      boolean passed,
      boolean active,
      boolean cancelled,
      List<String> images,
      CoordinatesResponse coordinates) {}

  public record EventDetailResponse(
      String id,
      String name,
      String description,
      String city,
      String address,
      List<String> keywords,
      Category category,
      CountryCode countryCode,
      String host,
      boolean recurring,
      LocalDate date,
      WeekDays weekday,
      LocalTime startsAt,
      LocalTime endsAt,
      int capacity,
      int attendeeCount,
      boolean passed,
      boolean active,
      boolean cancelled,
      List<String> images,
      CoordinatesResponse coordinates,
      SourceAttributionResponse source) {}

  public record SourceAttributionResponse(String sourceName, String sourceUrl) {}

  public record CoordinatesRequest(
      @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
      @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude) {}

  public record CoordinatesResponse(Double latitude, Double longitude) {}
}
