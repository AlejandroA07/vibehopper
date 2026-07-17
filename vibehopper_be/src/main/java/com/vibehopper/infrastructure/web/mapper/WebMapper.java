package com.vibehopper.infrastructure.web.mapper;

import com.vibehopper.application.event.query.EventPage;
import com.vibehopper.domain.event.Coordinates;
import com.vibehopper.domain.event.Event;
import com.vibehopper.domain.event.EventSource;
import com.vibehopper.domain.event.SourceType;
import com.vibehopper.domain.user.AppUser;
import com.vibehopper.infrastructure.web.dto.EventDtos;
import com.vibehopper.infrastructure.web.dto.UserResponse;
import java.util.List;

public final class WebMapper {
  private WebMapper() {}

  public static Event toDomain(EventDtos.CreateEventRequest request) {
    return Event.builder()
        .city(requiredText(request.city()))
        .description(optionalText(request.description()))
        .address(requiredText(request.address()))
        .name(requiredText(request.name()))
        .keywords(cleanList(request.keywords()))
        .category(request.category())
        .countryCode(request.countryCode())
        .host(optionalText(request.host()))
        .recurring(request.recurring())
        .date(request.date())
        .weekday(request.weekday())
        .startsAt(request.startsAt())
        .endsAt(request.endsAt())
        .capacity(request.capacity())
        .images(cleanList(request.images()))
        .coordinates(toDomainCoordinates(request.coordinates()))
        .build();
  }

  public static Event toDomain(EventDtos.UpdateEventRequest request) {
    return Event.builder()
        .city(requiredText(request.city()))
        .description(optionalText(request.description()))
        .address(requiredText(request.address()))
        .name(requiredText(request.name()))
        .keywords(cleanList(request.keywords()))
        .category(request.category())
        .countryCode(request.countryCode())
        .host(optionalText(request.host()))
        .recurring(request.recurring())
        .date(request.date())
        .weekday(request.weekday())
        .startsAt(request.startsAt())
        .endsAt(request.endsAt())
        .capacity(request.capacity())
        .images(cleanList(request.images()))
        .coordinates(toDomainCoordinates(request.coordinates()))
        .build();
  }

  public static EventDtos.EventPageResponse toPageResponse(EventPage page) {
    return new EventDtos.EventPageResponse(
        page.items().stream().map(WebMapper::toSummaryResponse).toList(),
        page.page(),
        page.size(),
        page.totalItems(),
        page.totalPages());
  }

  public static EventDtos.EventSummaryResponse toSummaryResponse(Event event) {
    return new EventDtos.EventSummaryResponse(
        event.id(),
        event.name(),
        event.city(),
        event.address(),
        event.category(),
        event.countryCode(),
        event.host(),
        event.recurring(),
        event.date(),
        event.startsAt(),
        event.endsAt(),
        event.passed(),
        event.active(),
        event.cancelled(),
        event.images(),
        toResponseCoordinates(event.coordinates()));
  }

  public static EventDtos.EventDetailResponse toDetailResponse(Event event) {
    return new EventDtos.EventDetailResponse(
        event.id(),
        event.name(),
        event.description(),
        event.city(),
        event.address(),
        event.keywords(),
        event.category(),
        event.countryCode(),
        event.host(),
        event.recurring(),
        event.date(),
        event.weekday(),
        event.startsAt(),
        event.endsAt(),
        event.capacity(),
        event.attendees().size(),
        event.passed(),
        event.active(),
        event.cancelled(),
        event.images(),
        toResponseCoordinates(event.coordinates()),
        toSourceAttribution(event.source()));
  }

  public static UserResponse toResponse(AppUser user) {
    return new UserResponse(
        user.id(), user.name(), user.email(), user.role(), user.locked(), user.enabled());
  }

  private static Coordinates toDomainCoordinates(EventDtos.CoordinatesRequest request) {
    return request == null ? null : new Coordinates(request.latitude(), request.longitude());
  }

  private static EventDtos.CoordinatesResponse toResponseCoordinates(Coordinates coordinates) {
    return coordinates == null
        ? null
        : new EventDtos.CoordinatesResponse(coordinates.latitude(), coordinates.longitude());
  }

  private static EventDtos.SourceAttributionResponse toSourceAttribution(EventSource source) {
    if (source == null || source.sourceType() == SourceType.MANUAL) {
      return null;
    }
    return new EventDtos.SourceAttributionResponse(source.sourceName(), source.sourceUrl());
  }

  private static String requiredText(String value) {
    return value.trim();
  }

  private static String optionalText(String value) {
    if (value == null) {
      return null;
    }
    String normalized = value.trim();
    return normalized.isEmpty() ? null : normalized;
  }

  private static List<String> cleanList(List<String> values) {
    return values == null ? List.of() : values.stream().map(String::trim).toList();
  }
}
