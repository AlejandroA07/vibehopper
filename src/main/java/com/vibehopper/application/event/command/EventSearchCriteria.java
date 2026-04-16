package com.vibehopper.application.event.command;
import com.vibehopper.domain.event.Category; import java.time.LocalDate;
public record EventSearchCriteria(String city, Category category, LocalDate from, LocalDate to, Double latitude, Double longitude, Double radiusKm) {
    public boolean hasGeoSearch() { return latitude != null || longitude != null || radiusKm != null; }
}
