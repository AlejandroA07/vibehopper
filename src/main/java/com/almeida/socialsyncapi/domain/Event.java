package com.almeida.socialsyncapi.domain;

import com.almeida.socialsyncapi.domain.enums.Category;
import com.almeida.socialsyncapi.domain.enums.CountryCode;
import com.almeida.socialsyncapi.domain.enums.WeekDays;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Document(collection = "events")
public class Event {
    @Id
    private String eventId;
    private String city;
    private String description;
    private String location;
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

    public Event() {}

    private Event(EventBuilder builder) {
        this.eventId = builder.eventId;
        this.city = builder.city;
        this.description = builder.description;
        this.location = builder.location;
        this.name = builder.name;
        this.keywords = builder.keywords;
        this.category = builder.category;
        this.countryCode = builder.countryCode;
        this.host = builder.host;
        this.ownerId = builder.ownerId;
        this.created = builder.created;
        this.updated = builder.updated;
        this.recurring = builder.recurring;
        this.date = builder.date;
        this.weekday = builder.weekday;
        this.startsAt = builder.startsAt;
        this.endsAt = builder.endsAt;
        this.capacity = builder.capacity;
        this.passed = builder.passed;
        this.active = builder.active;
        this.cancelled = builder.cancelled;
        this.images = builder.images;
        this.attendees = builder.attendees;
    }


    public static class EventBuilder {
        private String eventId;
        private String city;
        private String description;
        private String location;
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

        public EventBuilder eventId(String eventId) {
            this.eventId = eventId;
            return this;
        }

        public EventBuilder city(String city) {
            this.city = city;
            return this;
        }

        public EventBuilder description(String description) {
            this.description = description;
            return this;
        }

        public EventBuilder location(String location) {
            this.location = location;
            return this;
        }

        public EventBuilder name(String name) {
            this.name = name;
            return this;
        }

        public EventBuilder keywords(List<String> keywords) {
            this.keywords = keywords;
            return this;
        }

        public EventBuilder category(Category category) {
            this.category = category;
            return this;
        }

        public EventBuilder countryCode(CountryCode countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public EventBuilder host(String host) {
            this.host = host;
            return this;
        }

        public EventBuilder ownerId(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public EventBuilder created(LocalDate created) {
            this.created = created;
            return this;
        }

        public EventBuilder updated(LocalDate updated) {
            this.updated = updated;
            return this;
        }

        public EventBuilder recurring(boolean recurring) {
            this.recurring = recurring;
            return this;
        }

        public EventBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public EventBuilder weekday(WeekDays weekday) {
            this.weekday = weekday;
            return this;
        }

        public EventBuilder startsAt(LocalTime startsAt) {
            this.startsAt = startsAt;
            return this;
        }

        public EventBuilder endsAt(LocalTime endsAt) {
            this.endsAt = endsAt;
            return this;
        }

        public EventBuilder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public EventBuilder passed(boolean passed) {
            this.passed = passed;
            return this;
        }

        public EventBuilder active(boolean active) {
            this.active = active;
            return this;
        }

        public EventBuilder cancelled(boolean cancelled) {
            this.cancelled = cancelled;
            return this;
        }

        public EventBuilder images(List<String> images) {
            this.images = images;
            return this;
        }

        public EventBuilder attendees(List<String> attendees) {
            this.attendees = attendees;
            return this;
        }

        public Event build() {
            return new Event(this);
        }
    }

    //Method to obtain builder
    public static EventBuilder builder() {
        return new EventBuilder();
    }

    // Getters and Setters for the main class
    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public CountryCode getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(CountryCode countryCode) {
        this.countryCode = countryCode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public LocalDate getCreated() {
        return created;
    }

    public void setCreated(LocalDate created) {
        this.created = created;
    }

    public LocalDate getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDate updated) {
        this.updated = updated;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public WeekDays getWeekday() {
        return weekday;
    }

    public void setWeekday(WeekDays weekday) {
        this.weekday = weekday;
    }

    public LocalTime getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(LocalTime startsAt) {
        this.startsAt = startsAt;
    }

    public LocalTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalTime endsAt) {
        this.endsAt = endsAt;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public List<String> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<String> attendees) {
        this.attendees = attendees;
    }
}
