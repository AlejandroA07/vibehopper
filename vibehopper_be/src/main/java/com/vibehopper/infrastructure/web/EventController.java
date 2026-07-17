package com.vibehopper.infrastructure.web;

import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.application.event.usecase.EventUseCase;
import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.user.Role;
import com.vibehopper.infrastructure.security.UserPrincipal;
import com.vibehopper.infrastructure.web.dto.EventDtos;
import com.vibehopper.infrastructure.web.mapper.WebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events")
@Validated
@Tag(name = "Events")
public class EventController {
  private static final String BEARER_AUTH = "bearerAuth";
  private final EventUseCase events;

  public EventController(EventUseCase events) {
    this.events = events;
  }

  @GetMapping
  @Operation(
      operationId = "listEvents",
      summary = "List and filter public events",
      description = "Returns a zero-based, bounded page of public event summaries.")
  public ResponseEntity<EventDtos.EventPageResponse> findAll(
      @RequestParam(required = false) @Size(max = 120) String city,
      @RequestParam(required = false) Category category,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(required = false, name = "lat") @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
      @RequestParam(required = false, name = "lng") @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
      @RequestParam(required = false) @DecimalMin("0.1") @DecimalMax("500.0") Double radiusKm,
      @RequestParam(defaultValue = "0") @Min(0) @Max(10_000) int page,
      @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size) {
    return ResponseEntity.ok(
        WebMapper.toPageResponse(
            events.search(
                new EventSearchCriteria(
                    city, category, from, to, latitude, longitude, radiusKm, page, size))));
  }

  @GetMapping("/{id}")
  @Operation(operationId = "getEvent", summary = "Get a public event")
  public ResponseEntity<EventDtos.EventDetailResponse> findById(@PathVariable String id) {
    return ResponseEntity.ok(WebMapper.toDetailResponse(events.findById(id)));
  }

  @PostMapping
  @Operation(
      operationId = "createEvent",
      summary = "Create an event",
      description = "Requires bearer authentication and a valid CSRF cookie/header pair.",
      security = @SecurityRequirement(name = BEARER_AUTH))
  public ResponseEntity<EventDtos.EventDetailResponse> create(
      @Valid @RequestBody EventDtos.CreateEventRequest request,
      @AuthenticationPrincipal UserPrincipal principal) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            WebMapper.toDetailResponse(events.create(WebMapper.toDomain(request), principal.id())));
  }

  @PutMapping("/{id}")
  @Operation(
      operationId = "replaceEvent",
      summary = "Replace an event's editable fields",
      description =
          "Requires bearer authentication, a valid CSRF cookie/header pair, and ownership or ADMIN.",
      security = @SecurityRequirement(name = BEARER_AUTH))
  public ResponseEntity<EventDtos.EventDetailResponse> update(
      @PathVariable String id,
      @Valid @RequestBody EventDtos.UpdateEventRequest request,
      @AuthenticationPrincipal UserPrincipal principal) {
    return ResponseEntity.ok(
        WebMapper.toDetailResponse(
            events.update(id, WebMapper.toDomain(request), principal.id(), isAdmin(principal))));
  }

  @DeleteMapping("/{id}")
  @Operation(
      operationId = "deleteEvent",
      summary = "Delete an event",
      description =
          "Requires bearer authentication, a valid CSRF cookie/header pair, and ownership or ADMIN.",
      security = @SecurityRequirement(name = BEARER_AUTH))
  public ResponseEntity<Void> delete(
      @PathVariable String id, @AuthenticationPrincipal UserPrincipal principal) {
    events.deleteById(id, principal.id(), isAdmin(principal));
    return ResponseEntity.noContent().build();
  }

  private static boolean isAdmin(UserPrincipal principal) {
    return principal.user().role() == Role.ADMIN;
  }
}
