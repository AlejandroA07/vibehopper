package com.almeida.socialsyncapi.infrastructure.dto;

import com.almeida.socialsyncapi.domain.enums.Category;
import com.almeida.socialsyncapi.domain.enums.CountryCode;
import com.almeida.socialsyncapi.domain.enums.WeekDays;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record EventDTO(
    String eventId,
    @Size(max = 120) String city,
    @Size(max = 2000) String description,
    @Size(max = 240) String location,
    @Size(max = 160) String name,
    List<String> keywords,
    Category category,
    CountryCode countryCode,
    @Size(max = 160) String host,
    String ownerId,
    boolean recurring,
    LocalDate date,
    WeekDays weekday,
    LocalTime startsAt,
    LocalTime endsAt,
    @PositiveOrZero int capacity,
    List<String> images) {}
