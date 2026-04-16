package com.vibehopper.infrastructure.persistence.event;
import com.vibehopper.application.event.command.EventSearchCriteria; import com.vibehopper.application.event.port.EventRepositoryPort; import com.vibehopper.domain.event.Event; import org.springframework.data.geo.Point; import org.springframework.data.mongodb.core.MongoTemplate; import org.springframework.data.mongodb.core.query.*; import org.springframework.stereotype.Repository; import java.text.Normalizer; import java.time.LocalDate; import java.util.*;
@Repository public class EventPersistenceAdapter implements EventRepositoryPort {
    private static final double EARTH_RADIUS_KM=6378.1; private final SpringDataEventRepository repository; private final MongoTemplate mongoTemplate;
    public EventPersistenceAdapter(SpringDataEventRepository repository, MongoTemplate mongoTemplate){this.repository=repository;this.mongoTemplate=mongoTemplate;}
    public Event save(Event event){ EventDocument d=EventPersistenceMapper.toDocument(event,normalize(event.name()),normalize(event.city())); return EventPersistenceMapper.toDomain(repository.save(d)); }
    public List<Event> findAll(){ return repository.findAll().stream().map(EventPersistenceMapper::toDomain).toList(); }
    public Optional<Event> findById(String id){ return repository.findById(id).map(EventPersistenceMapper::toDomain); }
    public void deleteById(String id){ repository.deleteById(id); }
    public List<Event> search(EventSearchCriteria c){ Query q=new Query(); if(c.city()!=null&&!c.city().isBlank())q.addCriteria(Criteria.where("normalizedCity").is(normalize(c.city()))); if(c.category()!=null)q.addCriteria(Criteria.where("category").is(c.category())); if(c.from()!=null||c.to()!=null){ Criteria dc=Criteria.where("date"); if(c.from()!=null)dc=dc.gte(c.from()); if(c.to()!=null)dc=dc.lte(c.to()); q.addCriteria(dc);} if(c.hasGeoSearch())q.addCriteria(Criteria.where("coordinates").nearSphere(new Point(c.longitude(),c.latitude())).maxDistance(c.radiusKm()/EARTH_RADIUS_KM)); return mongoTemplate.find(q,EventDocument.class).stream().map(EventPersistenceMapper::toDomain).toList(); }
    public boolean existsBySource(String sourceName,String externalId){ return repository.existsBySource_SourceNameAndSource_ExternalId(sourceName,externalId); }
    public boolean existsByNormalizedIdentity(String normalizedName, LocalDate date, String city){ return repository.existsByNormalizedNameAndDateAndNormalizedCity(normalizedName,date,city); }
    private String normalize(String value){ if(value==null)return null; String n=Normalizer.normalize(value,Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT).trim().replaceAll("\\s+"," "); return n.isBlank()?null:n; }
}
