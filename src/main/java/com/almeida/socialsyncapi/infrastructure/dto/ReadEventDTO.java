package com.almeida.socialsyncapi.infrastructure.dto;

import com.almeida.socialsyncapi.domain.enums.Category;

import java.time.LocalDate;
import java.util.List;

public record ReadEventDTO(
        String eventId,
        String city,
        String description,
        String location,
        String name,
        List<String> keywords,
        Category category,
        String host,
        boolean recurring,
        LocalDate date,
        int capacity,
        boolean passed,
        boolean active,
        boolean cancelled,
        List<String> images,
        List<String> attendees

) {
}
