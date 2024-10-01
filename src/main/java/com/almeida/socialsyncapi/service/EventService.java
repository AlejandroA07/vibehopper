package com.almeida.socialsyncapi.service;

import com.almeida.socialsyncapi.domain.AppUser;
import com.almeida.socialsyncapi.domain.Event;
import com.almeida.socialsyncapi.infrastructure.dto.EventDTO;
import com.almeida.socialsyncapi.infrastructure.EntityMapper.EventDTOMapper;
import com.almeida.socialsyncapi.repository.AppUserRepository;
import com.almeida.socialsyncapi.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final AppUserRepository appUserRepository;
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    public EventService(EventRepository eventRepository, EventDTOMapper eventDTOMapper, AppUserRepository appUserRepository) {
        this.eventRepository = eventRepository;
        this.appUserRepository = appUserRepository;
    }

    //CREATE
    public EventDTO createEvent(EventDTO eventDTO) {
        String ownerId = getCurrentUserId();
        Event event = EventDTOMapper.toEvent(eventDTO);
        event.setOwnerId(ownerId);
        Event savedEvent = eventRepository.save(event);
        return EventDTOMapper.toEventDTO(savedEvent);
    }

    //GET ALL
    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(EventDTOMapper::toEventDTO)
                .collect(Collectors.toList());
    }

    //GET BY ID
    public Optional<EventDTO> getEventById(String id) {

        return eventRepository.findById(id)
                .map(EventDTOMapper::toEventDTO);
    }

    //UPDATE
    public EventDTO updateEvent(EventDTO eventRequest) {
        Event existingEvent = eventRepository.findById(eventRequest.eventId())
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventRequest.eventId()));

        logger.info("Updating event with id: {}", existingEvent.getEventId());

        Event updatedEvent = EventDTOMapper.updateEventFromRequest(eventRequest, existingEvent);
        eventRepository.save(updatedEvent);
        logger.info("Event updated successfully: {}", updatedEvent);

        return EventDTOMapper.toEventDTO(updatedEvent);

    }

    //DELETE
    public void deleteById(String id) { eventRepository.deleteById(id); }


    private String getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();

            AppUser user = appUserRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));
            return user.getId();
        }
        throw new IllegalStateException("User is not authenticated");
    }

}
