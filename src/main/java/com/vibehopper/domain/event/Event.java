package com.vibehopper.domain.event;
import java.time.LocalDate; import java.time.LocalTime; import java.util.List;
public record Event(
        String id, String city, String description, String address, String name, List<String> keywords,
        Category category, CountryCode countryCode, String host, String ownerId, LocalDate created, LocalDate updated,
        boolean recurring, LocalDate date, WeekDays weekday, LocalTime startsAt, LocalTime endsAt, int capacity,
        boolean passed, boolean active, boolean cancelled, List<String> images, List<String> attendees,
        EventSource source, Coordinates coordinates) {}
