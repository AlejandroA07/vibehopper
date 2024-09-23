package com.almeida.socialsyncapi.EntityMapper;

import com.almeida.socialsyncapi.domain.Event;
import com.almeida.socialsyncapi.dto.EventOwnerDTO;
import org.springframework.stereotype.Service;

@Service
public class EventOwnerDTOMapper {
    public EventOwnerDTO toEventOwnerDTO(Event event) {
        if (event == null) {
            return null;
        }

        return new EventOwnerDTO(
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

    public Event toEvent(EventOwnerDTO dto) {
        if (dto == null) {
            return null;
        }

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
}
