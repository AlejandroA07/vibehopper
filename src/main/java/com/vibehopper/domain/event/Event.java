package com.vibehopper.domain.event;
import java.time.LocalDate; import java.time.LocalTime; import java.util.List;
public record Event(
        String id, String city, String description, String address, String name, List<String> keywords,
        Category category, CountryCode countryCode, String host, String ownerId, LocalDate created, LocalDate updated,
        boolean recurring, LocalDate date, WeekDays weekday, LocalTime startsAt, LocalTime endsAt, int capacity,
        boolean passed, boolean active, boolean cancelled, List<String> images, List<String> attendees,
        EventSource source, Coordinates coordinates) {

    public Event {
        keywords  = keywords  != null ? keywords  : List.of();
        images    = images    != null ? images    : List.of();
        attendees = attendees != null ? attendees : List.of();
    }

    public static Builder builder() { return new Builder(); }

    public Builder toBuilder() {
        return new Builder()
                .id(id).city(city).description(description).address(address).name(name)
                .keywords(keywords).category(category).countryCode(countryCode).host(host)
                .ownerId(ownerId).created(created).updated(updated).recurring(recurring)
                .date(date).weekday(weekday).startsAt(startsAt).endsAt(endsAt).capacity(capacity)
                .passed(passed).active(active).cancelled(cancelled).images(images)
                .attendees(attendees).source(source).coordinates(coordinates);
    }

    public static final class Builder {
        private String id;
        private String city;
        private String description;
        private String address;
        private String name;
        private List<String> keywords;
        private Category category;
        private CountryCode countryCode;
        private String host;
        private String ownerId;
        private LocalDate created;
        private LocalDate updated;
        private boolean recurring;
        private LocalDate date;
        private WeekDays weekday;
        private LocalTime startsAt;
        private LocalTime endsAt;
        private int capacity;
        private boolean passed;
        private boolean active;
        private boolean cancelled;
        private List<String> images;
        private List<String> attendees;
        private EventSource source;
        private Coordinates coordinates;

        public Builder id(String id)                        { this.id = id; return this; }
        public Builder city(String city)                    { this.city = city; return this; }
        public Builder description(String description)      { this.description = description; return this; }
        public Builder address(String address)              { this.address = address; return this; }
        public Builder name(String name)                    { this.name = name; return this; }
        public Builder keywords(List<String> keywords)      { this.keywords = keywords; return this; }
        public Builder category(Category category)          { this.category = category; return this; }
        public Builder countryCode(CountryCode countryCode) { this.countryCode = countryCode; return this; }
        public Builder host(String host)                    { this.host = host; return this; }
        public Builder ownerId(String ownerId)              { this.ownerId = ownerId; return this; }
        public Builder created(LocalDate created)           { this.created = created; return this; }
        public Builder updated(LocalDate updated)           { this.updated = updated; return this; }
        public Builder recurring(boolean recurring)         { this.recurring = recurring; return this; }
        public Builder date(LocalDate date)                 { this.date = date; return this; }
        public Builder weekday(WeekDays weekday)            { this.weekday = weekday; return this; }
        public Builder startsAt(LocalTime startsAt)         { this.startsAt = startsAt; return this; }
        public Builder endsAt(LocalTime endsAt)             { this.endsAt = endsAt; return this; }
        public Builder capacity(int capacity)               { this.capacity = capacity; return this; }
        public Builder passed(boolean passed)               { this.passed = passed; return this; }
        public Builder active(boolean active)               { this.active = active; return this; }
        public Builder cancelled(boolean cancelled)         { this.cancelled = cancelled; return this; }
        public Builder images(List<String> images)          { this.images = images; return this; }
        public Builder attendees(List<String> attendees)    { this.attendees = attendees; return this; }
        public Builder source(EventSource source)           { this.source = source; return this; }
        public Builder coordinates(Coordinates coordinates) { this.coordinates = coordinates; return this; }

        public Event build() {
            return new Event(id, city, description, address, name, keywords, category, countryCode,
                    host, ownerId, created, updated, recurring, date, weekday, startsAt, endsAt,
                    capacity, passed, active, cancelled, images, attendees, source, coordinates);
        }
    }
}
