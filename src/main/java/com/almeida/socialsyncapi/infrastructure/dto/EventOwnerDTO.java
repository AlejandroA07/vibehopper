package com.almeida.socialsyncapi.infrastructure.dto;

import com.almeida.socialsyncapi.domain.enums.Category;
import com.almeida.socialsyncapi.domain.enums.CountryCode;
import com.almeida.socialsyncapi.domain.enums.WeekDays;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record EventOwnerDTO(
        String eventId,
        String city,
        String description,
        String location,
        String name,
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
        List<String> images
) {
}
