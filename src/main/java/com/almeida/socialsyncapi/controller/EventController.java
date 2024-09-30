package com.almeida.socialsyncapi.controller;

import com.almeida.socialsyncapi.infrastructure.dto.EventOwnerDTO;
import com.almeida.socialsyncapi.infrastructure.dto.ReadEventDTO;
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
    public ResponseEntity<EventOwnerDTO> createEvent(@RequestBody EventOwnerDTO eventOwnerDTO) {
        EventOwnerDTO newEvent = eventService.createEvent(eventOwnerDTO);
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }

    @GetMapping("/events")
    public ResponseEntity<List<ReadEventDTO>> getAllEvents() {
        List<ReadEventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadEventDTO> getEventById(@PathVariable String id) {
        Optional<ReadEventDTO> readEventDTO = eventService.getEventById(id);
        return readEventDTO.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<EventOwnerDTO> updateEvent(@RequestBody EventOwnerDTO eventRequest) {
        EventOwnerDTO updatedEvent = eventService.updateEvent(eventRequest);
        return ResponseEntity.ok(updatedEvent);
    }


    @DeleteMapping("/del/event/{id}")
    public ResponseEntity<Void> deleteEventById(@PathVariable String id) {
        eventService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
