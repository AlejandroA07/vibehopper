package com.almeida.socialsyncapi.infrastructure.EntityMapper;

import com.almeida.socialsyncapi.domain.Event;
import com.almeida.socialsyncapi.infrastructure.dto.EventDTO;
import org.springframework.stereotype.Service;

@Service
public class EventDTOMapper {
  public static EventDTO toEventDTO(Event event) {

    return new EventDTO(
        event.getEventId(),
        event.getCity(),
        event.getDescription(),
        event.getLocation(),
        event.getName(),
        event.getKeywords(),
        event.getCategory(),
        event.getCountryCode(),
        event.getHost(),
        event.getOwnerId(),
        event.isRecurring(),
        event.getDate(),
        event.getWeekday(),
        event.getStartsAt(),
        event.getEndsAt(),
        event.getCapacity(),
        event.getImages());
  }

  public static Event toEvent(EventDTO dto) {

    return Event.builder()
        .eventId(dto.eventId())
        .city(dto.city())
        .description(dto.description())
        .location(dto.location())
        .name(dto.name())
        .keywords(dto.keywords())
        .category(dto.category())
        .countryCode(dto.countryCode())
        .host(dto.host())
        .ownerId(dto.ownerId())
        .recurring(dto.recurring())
        .date(dto.date())
        .weekday(dto.weekday())
        .startsAt(dto.startsAt())
        .endsAt(dto.endsAt())
        .capacity(dto.capacity())
        .images(dto.images())
        .build();
  }

  public static Event updateEventFromRequest(EventDTO eventRequest, Event existingEvent) {
    return Event.builder()
        .eventId(existingEvent.getEventId())
        .city(firstNonNull(eventRequest.city(), existingEvent.getCity()))
        .description(firstNonNull(eventRequest.description(), existingEvent.getDescription()))
        .location(firstNonNull(eventRequest.location(), existingEvent.getLocation()))
        .name(firstNonNull(eventRequest.name(), existingEvent.getName()))
        .keywords(firstNonNull(eventRequest.keywords(), existingEvent.getKeywords()))
        .category(firstNonNull(eventRequest.category(), existingEvent.getCategory()))
        .countryCode(firstNonNull(eventRequest.countryCode(), existingEvent.getCountryCode()))
        .host(firstNonNull(eventRequest.host(), existingEvent.getHost()))
        .ownerId(existingEvent.getOwnerId())
        .created(existingEvent.getCreated())
        .updated(existingEvent.getUpdated())
        .recurring(eventRequest.recurring())
        .date(firstNonNull(eventRequest.date(), existingEvent.getDate()))
        .weekday(firstNonNull(eventRequest.weekday(), existingEvent.getWeekday()))
        .startsAt(firstNonNull(eventRequest.startsAt(), existingEvent.getStartsAt()))
        .endsAt(firstNonNull(eventRequest.endsAt(), existingEvent.getEndsAt()))
        .capacity(
            eventRequest.capacity() > 0 ? eventRequest.capacity() : existingEvent.getCapacity())
        .passed(existingEvent.isPassed())
        .active(existingEvent.isActive())
        .cancelled(existingEvent.isCancelled())
        .images(firstNonNull(eventRequest.images(), existingEvent.getImages()))
        .attendees(existingEvent.getAttendees())
        .build();
  }

  private static <T> T firstNonNull(T requested, T existing) {
    return requested != null ? requested : existing;
  }
}
