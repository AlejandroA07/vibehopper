package com.vibehopper.application.event.usecase;

import com.vibehopper.application.common.ConflictException;
import com.vibehopper.application.common.NotFoundException;
import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.application.event.port.EventRepositoryPort;
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
            .attendees(event.attendees())
            .source(event.source() == null ? EventSource.manual() : event.source())
            .coordinates(event.coordinates())
            .build();
    if (deduplicationService.isDuplicate(toSave))
      throw new ConflictException("Event already exists");
    return eventRepository.save(toSave);
  }

  public List<Event> findAll() {
    return eventRepository.findAll();
  }

  public Event findById(String id) {
    return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found"));
  }

  public Event update(String id, Event request, String requesterId, boolean admin) {
    Event e = findById(id);
    ensureCanManage(e, requesterId, admin);
    Event updated =
        e.toBuilder()
            .city(v(request.city(), e.city()))
            .description(v(request.description(), e.description()))
            .address(v(request.address(), e.address()))
            .name(v(request.name(), e.name()))
            .keywords(v(request.keywords(), e.keywords()))
            .category(v(request.category(), e.category()))
            .countryCode(v(request.countryCode(), e.countryCode()))
            .host(v(request.host(), e.host()))
            .updated(LocalDate.now())
            .recurring(request.recurring())
            .date(v(request.date(), e.date()))
            .weekday(v(request.weekday(), e.weekday()))
            .startsAt(v(request.startsAt(), e.startsAt()))
            .endsAt(v(request.endsAt(), e.endsAt()))
            .capacity(request.capacity() > 0 ? request.capacity() : e.capacity())
            .images(v(request.images(), e.images()))
            .source(v(request.source(), e.source()))
            .coordinates(v(request.coordinates(), e.coordinates()))
            .build();
    return eventRepository.save(updated);
  }

  public void deleteById(String id, String requesterId, boolean admin) {
    Event e = findById(id);
    ensureCanManage(e, requesterId, admin);
    eventRepository.deleteById(id);
  }

  public List<Event> search(EventSearchCriteria criteria) {
    if (criteria.hasGeoSearch()
        && (criteria.latitude() == null
            || criteria.longitude() == null
            || criteria.radiusKm() == null))
      throw new IllegalArgumentException("lat, lng and radiusKm must be provided together");
    return eventRepository.search(criteria);
  }

  private static <T> T v(T value, T fallback) {
    return value == null ? fallback : value;
  }

  private static void ensureCanManage(Event event, String requesterId, boolean admin) {
    if (!admin && (requesterId == null || !requesterId.equals(event.ownerId()))) {
      throw new com.vibehopper.application.common.ForbiddenException(
          "You can only manage your own events");
    }
  }
}
