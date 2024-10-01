package com.almeida.socialsyncapi.controller;

import com.almeida.socialsyncapi.infrastructure.dto.EventDTO;
import com.almeida.socialsyncapi.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/event")
public class EventController {
    private final EventService eventService;

    @Autowired
    public EventController(EventService eventService) { this.eventService = eventService; }


    @PostMapping("/insert")
    public ResponseEntity<EventDTO> createEvent(@RequestBody EventDTO eventDTO) {
        EventDTO newEvent = eventService.createEvent(eventDTO);
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable String id) {
        Optional<EventDTO> EventDTO = eventService.getEventById(id);
        return EventDTO.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<EventDTO> updateEvent(@RequestBody EventDTO eventRequest) {
        EventDTO updatedEvent = eventService.updateEvent(eventRequest);
        return ResponseEntity.ok(updatedEvent);
    }


    @DeleteMapping("/del/{id}")
    public ResponseEntity<Void> deleteEventById(@PathVariable String id) {
        eventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
