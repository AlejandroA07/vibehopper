package com.almeida.socialsyncapi.infrastructure.EntityMapper;

import com.almeida.socialsyncapi.domain.Event;
import com.almeida.socialsyncapi.infrastructure.dto.EventDTO;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNullElse;

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
                event.isRecurring(),
                event.getDate(),
                event.getWeekday(),
                event.getStartsAt(),
                event.getEndsAt(),
                event.getCapacity(),
                event.getImages()
        );
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
                .city(requireNonNullElse(eventRequest.city(), existingEvent.getCity()))
                .description(requireNonNullElse(eventRequest.description(), existingEvent.getDescription()))
                .location(requireNonNullElse(eventRequest.location(), existingEvent.getLocation()))
                .name(requireNonNullElse(eventRequest.name(), existingEvent.getName()))
                .keywords(requireNonNullElse(eventRequest.keywords(), existingEvent.getKeywords()))
                .category(requireNonNullElse(eventRequest.category(), existingEvent.getCategory()))
                .countryCode(requireNonNullElse(eventRequest.countryCode(), existingEvent.getCountryCode()))
                .host(requireNonNullElse(eventRequest.host(), existingEvent.getHost()))
                .recurring(eventRequest.recurring())
                .date(requireNonNullElse(eventRequest.date(), existingEvent.getDate()))
                .weekday(requireNonNullElse(eventRequest.weekday(), existingEvent.getWeekday()))
                .startsAt(requireNonNullElse(eventRequest.startsAt(), existingEvent.getStartsAt()))
                .endsAt(requireNonNullElse(eventRequest.endsAt(), existingEvent.getEndsAt()))
                .capacity(eventRequest.capacity() > 0 ? eventRequest.capacity() : existingEvent.getCapacity())
                .images(requireNonNullElse(eventRequest.images(), existingEvent.getImages()))
                .build();
    }
}


