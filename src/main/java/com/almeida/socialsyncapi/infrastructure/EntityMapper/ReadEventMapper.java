package com.almeida.socialsyncapi.infrastructure.EntityMapper;

import com.almeida.socialsyncapi.domain.Event;
import com.almeida.socialsyncapi.infrastructure.dto.ReadEventDTO;
import org.springframework.stereotype.Service;

@Service
public class ReadEventMapper {

    public ReadEventDTO toDTO(Event event) {
        return new ReadEventDTO(
                event.getEventId(),
                event.getCity(),
                event.getDescription(),
                event.getLocation(),
                event.getName(),
                event.getKeywords(),
                event.getCategory(),
                event.getHost(),
                event.isRecurring(),
                event.getDate(),
                event.getCapacity(),
                event.isPassed(),
                event.isActive(),
                event.isCancelled(),
                event.getImages(),
                event.getAttendees()
        );
    }


    public Event toEntity(ReadEventDTO dto) {
        return Event.builder()
                .eventId(dto.eventId())
                .city(dto.city())
                .description(dto.description())
                .location(dto.location())
                .name(dto.name())
                .keywords(dto.keywords())
                .category(dto.category())
                .host(dto.host())
                .recurring(dto.recurring())
                .date(dto.date())
                .capacity(dto.capacity())
                .passed(dto.passed())
                .active(dto.active())
                .cancelled(dto.cancelled())
                .images(dto.images())
                .attendees(dto.attendees())
                .build();
    }
}
