package com.vibehopper.application.event.usecase;
import com.vibehopper.application.common.ConflictException; import com.vibehopper.application.common.NotFoundException; import com.vibehopper.application.event.command.EventSearchCriteria; import com.vibehopper.application.event.port.EventRepositoryPort; import com.vibehopper.domain.event.Event; import com.vibehopper.domain.event.EventSource;
import java.time.LocalDate; import java.util.List;
public final class EventUseCase {
    private final EventRepositoryPort eventRepository; private final EventDeduplicationService deduplicationService;
    public EventUseCase(EventRepositoryPort eventRepository, EventDeduplicationService deduplicationService) { this.eventRepository=eventRepository; this.deduplicationService=deduplicationService; }
    public Event create(Event event, String ownerId) {
        LocalDate now=LocalDate.now();
        Event toSave=new Event(null,event.city(),event.description(),event.address(),event.name(),event.keywords(),event.category(),event.countryCode(),event.host(),ownerId,now,now,event.recurring(),event.date(),event.weekday(),event.startsAt(),event.endsAt(),event.capacity(),false,true,false,event.images(),event.attendees(),event.source()==null?EventSource.manual():event.source(),event.coordinates());
        if (deduplicationService.isDuplicate(toSave)) throw new ConflictException("Event already exists");
        return eventRepository.save(toSave);
    }
    public List<Event> findAll(){ return eventRepository.findAll(); }
    public Event findById(String id){ return eventRepository.findById(id).orElseThrow(() -> new NotFoundException("Event not found")); }
    public Event update(String id, Event request){
        Event e=findById(id);
        Event updated=new Event(e.id(),v(request.city(),e.city()),v(request.description(),e.description()),v(request.address(),e.address()),v(request.name(),e.name()),v(request.keywords(),e.keywords()),v(request.category(),e.category()),v(request.countryCode(),e.countryCode()),v(request.host(),e.host()),e.ownerId(),e.created(),LocalDate.now(),request.recurring(),v(request.date(),e.date()),v(request.weekday(),e.weekday()),v(request.startsAt(),e.startsAt()),v(request.endsAt(),e.endsAt()),request.capacity()>0?request.capacity():e.capacity(),e.passed(),e.active(),e.cancelled(),v(request.images(),e.images()),e.attendees(),v(request.source(),e.source()),v(request.coordinates(),e.coordinates()));
        return eventRepository.save(updated);
    }
    public void deleteById(String id){ findById(id); eventRepository.deleteById(id); }
    public List<Event> search(EventSearchCriteria criteria){ if(criteria.hasGeoSearch()&&(criteria.latitude()==null||criteria.longitude()==null||criteria.radiusKm()==null)) throw new IllegalArgumentException("lat, lng and radiusKm must be provided together"); return eventRepository.search(criteria); }
    private static <T> T v(T value,T fallback){ return value==null?fallback:value; }
}
