package com.vibehopper.application.event.usecase;

import com.vibehopper.application.common.ConflictException;
import com.vibehopper.application.common.NotFoundException;
import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.application.event.port.EventRepositoryPort;
import com.vibehopper.application.event.query.EventPage;
import com.vibehopper.domain.event.Event;
import com.vibehopper.domain.event.EventSource;
import java.time.LocalDate;
import java.util.List;

public final class EventUseCase {
  private final EventRepositoryPort eventRepository;
  private final EventDeduplicationService deduplicationService;

  public EventUseCase(
      EventRepositoryPort eventRepository, EventDeduplicationService deduplicationService) {
    this.eventRepository = eventRepository;
    this.deduplicationService = deduplicationService;
  }

  public Event create(Event event, String ownerId) {
    LocalDate now = LocalDate.now();
    Event toSave =
        Event.builder()
            .city(event.city())
            .description(event.description())
            .address(event.address())
            .name(event.name())
            .keywords(event.keywords())
            .category(event.category())
            .countryCode(event.countryCode())
            .host(event.host())
            .ownerId(ownerId)
            .created(now)
            .updated(now)
            .recurring(event.recurring())
            .date(event.date())
            .weekday(event.weekday())
            .startsAt(event.startsAt())
            .endsAt(event.endsAt())
            .capacity(event.capacity())
            .passed(false)
            .active(true)
            .cancelled(false)
            .images(event.images())
            .attendees(List.of())
            .source(EventSource.manual())
            .coordinates(event.coordinates())
            .build();
    if (deduplicationService.isDuplicate(toSave))
      throw new ConflictException("Event already exists");
    return eventRepository.save(toSave);
  }

  public Event findById(String id) {
    return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
  }

  public Event update(String id, Event request, String requesterId, boolean admin) {
    Event e = findById(id);
    ensureCanManage(e, requesterId, admin);
    Event updated =
        e.toBuilder()
            .city(request.city())
            .description(request.description())
            .address(request.address())
            .name(request.name())
            .keywords(request.keywords())
            .category(request.category())
            .countryCode(request.countryCode())
            .host(request.host())
            .updated(LocalDate.now())
            .recurring(request.recurring())
            .date(request.date())
            .weekday(request.weekday())
            .startsAt(request.startsAt())
            .endsAt(request.endsAt())
            .capacity(request.capacity())
            .images(request.images())
            .source(e.source())
            .coordinates(request.coordinates())
            .build();
    return eventRepository.save(updated);
  }

  public void deleteById(String id, String requesterId, boolean admin) {
    Event e = findById(id);
    ensureCanManage(e, requesterId, admin);
    eventRepository.deleteById(id);
  }

  public EventPage search(EventSearchCriteria criteria) {
    if (criteria.hasGeoSearch()
        && (criteria.latitude() == null
            || criteria.longitude() == null
            || criteria.radiusKm() == null))
      throw new IllegalArgumentException("lat, lng and radiusKm must be provided together");
    if (criteria.from() != null && criteria.to() != null && criteria.from().isAfter(criteria.to()))
      throw new IllegalArgumentException("from must be on or before to");
    if (criteria.page() < 0 || criteria.page() > 10_000)
      throw new IllegalArgumentException("page must be between 0 and 10000");
    if (criteria.size() < 1 || criteria.size() > 100)
      throw new IllegalArgumentException("size must be between 1 and 100");
    return eventRepository.search(criteria);
  }

  private static void ensureCanManage(Event event, String requesterId, boolean admin) {
    if (!admin && (requesterId == null || !requesterId.equals(event.ownerId()))) {
      throw new com.vibehopper.application.common.ForbiddenException(
          "You can only manage your own events");
    }
  }
}
