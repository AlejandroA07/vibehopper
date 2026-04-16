package com.vibehopper.infrastructure.web.mapper;
import com.vibehopper.domain.event.*; import com.vibehopper.domain.user.AppUser; import com.vibehopper.infrastructure.web.dto.*;
public final class WebMapper { private WebMapper(){}
    public static Event toDomain(EventDtos.EventRequest r){ return new Event(null,r.city(),r.description(),r.address(),r.name(),r.keywords(),r.category(),r.countryCode(),r.host(),null,null,null,r.recurring(),r.date(),r.weekday(),r.startsAt(),r.endsAt(),r.capacity(),false,true,false,r.images(),null,toDomainSource(r.source()),toDomainCoordinates(r.coordinates())); }
    public static EventDtos.EventResponse toResponse(Event e){ return new EventDtos.EventResponse(e.id(),e.city(),e.description(),e.address(),e.name(),e.keywords(),e.category(),e.countryCode(),e.host(),e.ownerId(),e.created(),e.updated(),e.recurring(),e.date(),e.weekday(),e.startsAt(),e.endsAt(),e.capacity(),e.passed(),e.active(),e.cancelled(),e.images(),e.attendees(),toResponseSource(e.source()),toResponseCoordinates(e.coordinates())); }
    public static UserResponse toResponse(AppUser u){ return new UserResponse(u.id(),u.name(),u.email(),u.role(),u.locked(),u.enabled()); }
    private static EventSource toDomainSource(EventDtos.EventSourceRequest r){ return r==null?null:new EventSource(r.sourceType(),r.sourceName(),r.externalId(),r.sourceUrl(),r.lastSyncedAt()); }
    private static Coordinates toDomainCoordinates(EventDtos.CoordinatesRequest r){ return r==null||r.latitude()==null||r.longitude()==null?null:new Coordinates(r.latitude(),r.longitude()); }
    private static EventDtos.EventSourceResponse toResponseSource(EventSource s){ return s==null?null:new EventDtos.EventSourceResponse(s.sourceType(),s.sourceName(),s.externalId(),s.sourceUrl(),s.lastSyncedAt()); }
    private static EventDtos.CoordinatesResponse toResponseCoordinates(Coordinates c){ return c==null?null:new EventDtos.CoordinatesResponse(c.latitude(),c.longitude()); }
}
