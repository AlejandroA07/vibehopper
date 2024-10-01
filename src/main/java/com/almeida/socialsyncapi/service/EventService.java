package com.almeida.socialsyncapi.service;

import com.almeida.socialsyncapi.domain.Event;
import com.almeida.socialsyncapi.infrastructure.dto.EventDTO;
import com.almeida.socialsyncapi.infrastructure.EntityMapper.EventDTOMapper;
import com.almeida.socialsyncapi.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventDTOMapper eventDTOMapper;

    @Autowired
    public EventService(EventRepository eventRepository, EventDTOMapper eventDTOMapper) {
        this.eventRepository = eventRepository;
        this.eventDTOMapper = eventDTOMapper;
    }

    public EventDTO createEvent(EventDTO eventDTO) {
        Event event = eventDTOMapper.toEvent(eventDTO);
        Event savedEvent = eventRepository.save(event);
        return eventDTOMapper.toEventDTO(savedEvent);
    }

    public List<EventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(EventDTOMapper::toEventDTO)
                .collect(Collectors.toList());
    }

    public Optional<EventDTO> getEventById(String id) {

        return eventRepository.findById(id)
                .map(EventDTOMapper::toEventDTO);
    }

    public EventDTO updateEvent(EventDTO eventRequest) {
        Event existingEvent = eventRepository.findById(eventRequest.eventId())
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventRequest.eventId()));

        //Add logger

        Event updatedEvent = EventDTOMapper.updateEventFromRequest(eventRequest, existingEvent);
        eventRepository.save(updatedEvent);
        return EventDTOMapper.toEventDTO(updatedEvent);

    }

    //Delete Method
    public void deleteById(String id) { eventRepository.deleteById(id); }

}
