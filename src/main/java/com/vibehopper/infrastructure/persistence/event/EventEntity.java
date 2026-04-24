package com.vibehopper.infrastructure.persistence.event;

import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.event.CountryCode;
import com.vibehopper.domain.event.SourceType;
import com.vibehopper.domain.event.WeekDays;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    private UUID id;

    @Column(nullable = false)
    private String city;

    @Column(name = "normalized_city")
    private String normalizedCity;

    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String name;

    @Column(name = "normalized_name")
    private String normalizedName;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> keywords = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "country_code", nullable = false)
    private CountryCode countryCode;

    private String host;

    @Column(name = "owner_id")
    private UUID ownerId;

    private LocalDate created;
    private LocalDate updated;

    @Column(nullable = false)
    private boolean recurring;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private WeekDays weekday;

    @Column(name = "starts_at")
    private LocalTime startsAt;

    @Column(name = "ends_at")
    private LocalTime endsAt;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private boolean passed;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private boolean cancelled;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private List<String> images = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "event_attendees", joinColumns = @JoinColumn(name = "event_id"))
    @Column(name = "user_id", nullable = false)
    private List<UUID> attendees = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type")
    private SourceType sourceType;

    @Column(name = "source_name")
    private String sourceName;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @JdbcTypeCode(SqlTypes.GEOGRAPHY)
    @Column(name = "location", columnDefinition = "geography(Point, 4326)")
    private Point location;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getNormalizedCity() { return normalizedCity; }
    public void setNormalizedCity(String normalizedCity) { this.normalizedCity = normalizedCity; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getNormalizedName() { return normalizedName; }
    public void setNormalizedName(String normalizedName) { this.normalizedName = normalizedName; }
    public List<String> getKeywords() { return keywords; }
    public void setKeywords(List<String> keywords) { this.keywords = keywords == null ? new ArrayList<>() : new ArrayList<>(keywords); }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public CountryCode getCountryCode() { return countryCode; }
    public void setCountryCode(CountryCode countryCode) { this.countryCode = countryCode; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public UUID getOwnerId() { return ownerId; }
    public void setOwnerId(UUID ownerId) { this.ownerId = ownerId; }
    public LocalDate getCreated() { return created; }
    public void setCreated(LocalDate created) { this.created = created; }
    public LocalDate getUpdated() { return updated; }
    public void setUpdated(LocalDate updated) { this.updated = updated; }
    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public WeekDays getWeekday() { return weekday; }
    public void setWeekday(WeekDays weekday) { this.weekday = weekday; }
    public LocalTime getStartsAt() { return startsAt; }
    public void setStartsAt(LocalTime startsAt) { this.startsAt = startsAt; }
    public LocalTime getEndsAt() { return endsAt; }
    public void setEndsAt(LocalTime endsAt) { this.endsAt = endsAt; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images == null ? new ArrayList<>() : new ArrayList<>(images); }
    public List<UUID> getAttendees() { return attendees; }
    public void setAttendees(List<UUID> attendees) { this.attendees = attendees == null ? new ArrayList<>() : new ArrayList<>(attendees); }
    public SourceType getSourceType() { return sourceType; }
    public void setSourceType(SourceType sourceType) { this.sourceType = sourceType; }
    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    public String getExternalId() { return externalId; }
    public void setExternalId(String externalId) { this.externalId = externalId; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public Instant getLastSyncedAt() { return lastSyncedAt; }
    public void setLastSyncedAt(Instant lastSyncedAt) { this.lastSyncedAt = lastSyncedAt; }
    public Point getLocation() { return location; }
    public void setLocation(Point location) { this.location = location; }
}
