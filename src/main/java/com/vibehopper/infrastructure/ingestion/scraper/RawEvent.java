package com.vibehopper.infrastructure.ingestion.scraper;
import java.time.LocalDate; import java.time.LocalTime;
public record RawEvent(String title, String description, LocalDate date, LocalTime startsAt, LocalTime endsAt, String locationText, String sourceName, String sourceUrl, String externalId, String imageUrl) {}
