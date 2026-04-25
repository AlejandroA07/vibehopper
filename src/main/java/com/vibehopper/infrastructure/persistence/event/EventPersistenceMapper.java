package com.vibehopper.infrastructure.persistence.event;

import com.vibehopper.domain.event.Coordinates;
import com.vibehopper.domain.event.Event;
import com.vibehopper.domain.event.EventSource;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.UUID;

final class EventPersistenceMapper {
    private static final int WGS84_SRID = 4326;
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(new PrecisionModel(), WGS84_SRID);

    private EventPersistenceMapper() {}

    static Event toDomain(EventEntity entity) {
        return Event.builder()
                .id(entity.getId().toString())
                .city(entity.getCity()).description(entity.getDescription()).address(entity.getAddress())
                .name(entity.getName()).keywords(entity.getKeywords()).category(entity.getCategory())
                .countryCode(entity.getCountryCode()).host(entity.getHost())
                .ownerId(entity.getOwnerId() == null ? null : entity.getOwnerId().toString())
                .created(entity.getCreated()).updated(entity.getUpdated())
                .recurring(entity.isRecurring()).date(entity.getDate()).weekday(entity.getWeekday())
                .startsAt(entity.getStartsAt()).endsAt(entity.getEndsAt()).capacity(entity.getCapacity())
                .passed(entity.isPassed()).active(entity.isActive()).cancelled(entity.isCancelled())
                .images(entity.getImages()).attendees(toDomainAttendees(entity.getAttendees()))
                .source(toDomainSource(entity)).coordinates(toDomainCoordinates(entity.getLocation()))
                .build();
    }

    static EventEntity toEntity(Event event, String normalizedName, String normalizedCity) {
        EventEntity entity = new EventEntity();
        entity.setId(event.id() == null ? UUID.randomUUID() : UUID.fromString(event.id()));
        entity.setCity(event.city());
        entity.setNormalizedCity(normalizedCity);
        entity.setDescription(event.description());
        entity.setAddress(event.address());
        entity.setName(event.name());
        entity.setNormalizedName(normalizedName);
        entity.setKeywords(event.keywords());
        entity.setCategory(event.category());
        entity.setCountryCode(event.countryCode());
        entity.setHost(event.host());
        entity.setOwnerId(toUuid(event.ownerId()));
        entity.setCreated(event.created());
        entity.setUpdated(event.updated());
        entity.setRecurring(event.recurring());
        entity.setDate(event.date());
        entity.setWeekday(event.weekday());
        entity.setStartsAt(event.startsAt());
        entity.setEndsAt(event.endsAt());
        entity.setCapacity(event.capacity());
        entity.setPassed(event.passed());
        entity.setActive(event.active());
        entity.setCancelled(event.cancelled());
        entity.setImages(event.images());
        entity.setAttendees(toEntityAttendees(event.attendees()));
        setSource(entity, event.source());
        entity.setLocation(toEntityLocation(event.coordinates()));
        return entity;
    }

    static Point toEntityLocation(Coordinates coordinates) {
        if (coordinates == null) {
            return null;
        }
        Point point = GEOMETRY_FACTORY.createPoint(new Coordinate(coordinates.longitude(), coordinates.latitude()));
        point.setSRID(WGS84_SRID);
        return point;
    }

    static Coordinates toDomainCoordinates(Point point) {
        return point == null ? null : new Coordinates(point.getY(), point.getX());
    }

    private static EventSource toDomainSource(EventEntity entity) {
        if (entity.getSourceType() == null && entity.getSourceName() == null && entity.getExternalId() == null
                && entity.getSourceUrl() == null && entity.getLastSyncedAt() == null) {
            return null;
        }
        return new EventSource(entity.getSourceType(), entity.getSourceName(), entity.getExternalId(),
                entity.getSourceUrl(), entity.getLastSyncedAt());
    }

    private static void setSource(EventEntity entity, EventSource source) {
        if (source == null) {
            return;
        }
        entity.setSourceType(source.sourceType());
        entity.setSourceName(source.sourceName());
        entity.setExternalId(source.externalId());
        entity.setSourceUrl(source.sourceUrl());
        entity.setLastSyncedAt(source.lastSyncedAt());
    }

    private static List<String> toDomainAttendees(List<UUID> attendees) {
        return attendees == null ? List.of() : attendees.stream().map(UUID::toString).toList();
    }

    private static List<UUID> toEntityAttendees(List<String> attendees) {
        return attendees == null ? List.of() : attendees.stream().map(EventPersistenceMapper::toUuid).toList();
    }

    private static UUID toUuid(String id) {
        return id == null ? null : UUID.fromString(id);
    }
}
