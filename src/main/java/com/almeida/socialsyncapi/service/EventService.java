package com.almeida.socialsyncapi.service;

import com.almeida.socialsyncapi.domain.AppUser;
import com.almeida.socialsyncapi.domain.Event;
import com.almeida.socialsyncapi.domain.enums.Role;
import com.almeida.socialsyncapi.infrastructure.EntityMapper.EventDTOMapper;
import com.almeida.socialsyncapi.infrastructure.dto.EventDTO;
import com.almeida.socialsyncapi.infrastructure.exception.ForbiddenResourceException;
import com.almeida.socialsyncapi.infrastructure.exception.ResourceNotFoundException;
import com.almeida.socialsyncapi.repository.EventRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {

  private final EventRepository eventRepository;
  private final EventDTOMapper eventDTOMapper;

  @Autowired
  public EventService(EventRepository eventRepository, EventDTOMapper eventDTOMapper) {
    this.eventRepository = eventRepository;
    this.eventDTOMapper = eventDTOMapper;
  }

  public EventDTO createEvent(EventDTO eventDTO, AppUser owner) {
    Event event = eventDTOMapper.toEvent(eventDTO);
    event.setOwnerId(owner.getId());
    event.setCreated(LocalDate.now());
    event.setUpdated(LocalDate.now());
    event.setActive(true);
    Event savedEvent = eventRepository.save(event);
    return eventDTOMapper.toEventDTO(savedEvent);
  }

  public List<EventDTO> getAllEvents() {
    List<Event> events = eventRepository.findAll();
    return events.stream().map(EventDTOMapper::toEventDTO).collect(Collectors.toList());
  }

  public Optional<EventDTO> getEventById(String id) {

    return eventRepository.findById(id).map(EventDTOMapper::toEventDTO);
  }

  public EventDTO updateEvent(String id, EventDTO eventRequest, AppUser currentUser) {
    Event existingEvent =
        eventRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    ensureCanManageEvent(existingEvent, currentUser);

    Event updatedEvent = EventDTOMapper.updateEventFromRequest(eventRequest, existingEvent);
    updatedEvent.setUpdated(LocalDate.now());
    eventRepository.save(updatedEvent);
    return EventDTOMapper.toEventDTO(updatedEvent);
  }

  // Delete Method
  public void deleteById(String id, AppUser currentUser) {
    Event existingEvent =
        eventRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    ensureCanManageEvent(existingEvent, currentUser);
    eventRepository.deleteById(id);
  }

  private void ensureCanManageEvent(Event event, AppUser currentUser) {
    if (currentUser == null) {
      throw new ForbiddenResourceException("Forbidden");
    }

    boolean isAdmin = Role.ADMIN.equals(currentUser.getRole());
    boolean isOwner = event.getOwnerId() != null && event.getOwnerId().equals(currentUser.getId());
    if (!isAdmin && !isOwner) {
      throw new ForbiddenResourceException("Forbidden");
    }
  }
}
