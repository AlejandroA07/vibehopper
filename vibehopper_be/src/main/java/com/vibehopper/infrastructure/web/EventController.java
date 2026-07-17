package com.vibehopper.infrastructure.web;

import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.application.event.usecase.EventUseCase;
import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.user.Role;
import com.vibehopper.infrastructure.security.UserPrincipal;
import com.vibehopper.infrastructure.web.dto.EventDtos;
import com.vibehopper.infrastructure.web.mapper.WebMapper;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {
  private final EventUseCase events;

  public EventController(EventUseCase events) {
    this.events = events;
  }

  @GetMapping
  public ResponseEntity<List<EventDtos.EventResponse>> findAll() {
    return ResponseEntity.ok(events.findAll().stream().map(WebMapper::toResponse).toList());
  }

  @GetMapping("/search")
  public ResponseEntity<List<EventDtos.EventResponse>> search(
      @RequestParam(required = false) String city,
      @RequestParam(required = false) Category category,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(required = false, name = "lat") Double latitude,
      @RequestParam(required = false, name = "lng") Double longitude,
      @RequestParam(required = false) Double radiusKm) {
    return ResponseEntity.ok(
        events
            .search(
                new EventSearchCriteria(city, category, from, to, latitude, longitude, radiusKm))
            .stream()
            .map(WebMapper::toResponse)
            .toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<EventDtos.EventResponse> findById(@PathVariable String id) {
    return ResponseEntity.ok(WebMapper.toResponse(events.findById(id)));
  }

  @PostMapping
  public ResponseEntity<EventDtos.EventResponse> create(
      @Valid @RequestBody EventDtos.EventRequest r, @AuthenticationPrincipal UserPrincipal p) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(WebMapper.toResponse(events.create(WebMapper.toDomain(r), p.id())));
  }

  @PutMapping("/{id}")
  public ResponseEntity<EventDtos.EventResponse> update(
      @PathVariable String id,
      @Valid @RequestBody EventDtos.EventRequest r,
      @AuthenticationPrincipal UserPrincipal p) {
    return ResponseEntity.ok(
        WebMapper.toResponse(events.update(id, WebMapper.toDomain(r), p.id(), isAdmin(p))));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(
      @PathVariable String id, @AuthenticationPrincipal UserPrincipal p) {
    events.deleteById(id, p.id(), isAdmin(p));
    return ResponseEntity.noContent().build();
  }

  private static boolean isAdmin(UserPrincipal p) {
    return p.user().role() == Role.ADMIN;
  }
}
