package com.almeida.socialsyncapi.service.impl;

import com.almeida.socialsyncapi.domain.Event;
import com.almeida.socialsyncapi.dto.EventOwnerDTO;
import com.almeida.socialsyncapi.dto.ReadEventDTO;
import com.almeida.socialsyncapi.EntityMapper.EventOwnerDTOMapper;
import com.almeida.socialsyncapi.EntityMapper.ReadEventMapper;
import com.almeida.socialsyncapi.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final ReadEventMapper readEventMapper;
    private final EventOwnerDTOMapper eventOwnerDTOMapper;

    @Autowired
    public EventService(EventRepository eventRepository, ReadEventMapper readEventMapper, EventOwnerDTOMapper eventOwnerDTOMapper) {
        this.eventRepository = eventRepository;
        this.readEventMapper = readEventMapper;
        this.eventOwnerDTOMapper = eventOwnerDTOMapper;
    }

    public EventOwnerDTO createEvent(EventOwnerDTO eventOwnerDTO) {
        Event event = eventOwnerDTOMapper.toEvent(eventOwnerDTO);
        Event savedEvent = eventRepository.save(event);
        return eventOwnerDTOMapper.toEventOwnerDTO(savedEvent);
    }

    public List<ReadEventDTO> getAllEvents() {
        List<Event> events = eventRepository.findAll();
        return events.stream()
                .map(readEventMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ReadEventDTO> getEventById(String id) {

        return eventRepository.findById(id)
                .map(readEventMapper::toDTO);
    }

    //Delete Method
    public void deleteById(String id) { eventRepository.deleteById(id); }

}
