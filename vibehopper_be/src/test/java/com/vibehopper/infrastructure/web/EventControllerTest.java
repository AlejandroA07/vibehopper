package com.vibehopper.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.vibehopper.application.common.NotFoundException;
import com.vibehopper.application.event.command.EventSearchCriteria;
import com.vibehopper.application.event.query.EventPage;
import com.vibehopper.application.event.usecase.EventUseCase;
import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.event.Coordinates;
import com.vibehopper.domain.event.CountryCode;
import com.vibehopper.domain.event.Event;
import com.vibehopper.domain.event.EventSource;
import com.vibehopper.domain.event.SourceType;
import com.vibehopper.infrastructure.security.JwtService;
import com.vibehopper.infrastructure.security.RestAccessDeniedHandler;
import com.vibehopper.infrastructure.security.RestAuthenticationEntryPoint;
import com.vibehopper.infrastructure.security.UserPrincipal;
import com.vibehopper.infrastructure.web.dto.EventDtos;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = EventController.class,
    excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class,
    properties = "spring.jackson.deserialization.fail-on-unknown-properties=true")
@AutoConfigureMockMvc(addFilters = false)
@Import({RestAuthenticationEntryPoint.class, RestAccessDeniedHandler.class})
class EventControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockitoBean private JwtService jwtService;
  @MockitoBean private UserDetailsService userDetailsService;
  @MockitoBean private EventUseCase eventUseCase;

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void listEventsReturnsBoundedPageAndForwardsFilters() throws Exception {
    when(eventUseCase.search(any()))
        .thenReturn(new EventPage(List.of(event("event-1")), 2, 10, 24));

    mockMvc
        .perform(
            get("/api/events")
                .param("city", "Stockholm")
                .param("category", "MEETUP")
                .param("from", "2026-04-16")
                .param("to", "2026-04-17")
                .param("lat", "59.3")
                .param("lng", "18.0")
                .param("radiusKm", "10")
                .param("page", "2")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items[0].id").value("event-1"))
        .andExpect(jsonPath("$.page").value(2))
        .andExpect(jsonPath("$.size").value(10))
        .andExpect(jsonPath("$.totalItems").value(24))
        .andExpect(jsonPath("$.totalPages").value(3));

    ArgumentCaptor<EventSearchCriteria> criteria =
        ArgumentCaptor.forClass(EventSearchCriteria.class);
    verify(eventUseCase).search(criteria.capture());
    assertThat(criteria.getValue().city()).isEqualTo("Stockholm");
    assertThat(criteria.getValue().radiusKm()).isEqualTo(10.0);
    assertThat(criteria.getValue().page()).isEqualTo(2);
    assertThat(criteria.getValue().size()).isEqualTo(10);
  }

  @Test
  void publicEventDetailDoesNotExposePrivateOrIngestionIdentityFields() throws Exception {
    when(eventUseCase.findById("event-1")).thenReturn(event("event-1"));

    mockMvc
        .perform(get("/api/events/event-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("event-1"))
        .andExpect(jsonPath("$.attendeeCount").value(2))
        .andExpect(jsonPath("$.source.sourceName").value("community-calendar"))
        .andExpect(jsonPath("$.source.sourceUrl").value("https://example.com/events/1"))
        .andExpect(jsonPath("$.ownerId").doesNotExist())
        .andExpect(jsonPath("$.attendees").doesNotExist())
        .andExpect(jsonPath("$.source.externalId").doesNotExist())
        .andExpect(jsonPath("$.source.lastSyncedAt").doesNotExist());
  }

  @Test
  void getEventReturnsNotFoundErrorShape() throws Exception {
    when(eventUseCase.findById("missing")).thenThrow(new NotFoundException("Event not found"));

    mockMvc
        .perform(get("/api/events/missing"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Event not found"));
  }

  @Test
  void createEventUsesAuthenticatedPrincipalAndSafeWriteMapping() throws Exception {
    when(eventUseCase.create(any(Event.class), eq("user-1"))).thenReturn(event("event-1"));
    SecurityContextHolder.getContext().setAuthentication(authentication());

    mockMvc
        .perform(
            post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest())))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("event-1"));

    ArgumentCaptor<Event> event = ArgumentCaptor.forClass(Event.class);
    verify(eventUseCase).create(event.capture(), eq("user-1"));
    assertThat(event.getValue().source()).isNull();
    assertThat(event.getValue().attendees()).isEmpty();
  }

  @Test
  void createEventRejectsUnknownSourceInput() throws Exception {
    SecurityContextHolder.getContext().setAuthentication(authentication());
    ObjectNode payload = objectMapper.valueToTree(validCreateRequest());
    payload.putObject("source").put("externalId", "client-controlled");

    mockMvc
        .perform(
            post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Malformed request body"));
  }

  @Test
  void updateEventUsesPathIdAndUpdateContract() throws Exception {
    when(eventUseCase.update(eq("event-1"), any(Event.class), eq("user-1"), eq(false)))
        .thenReturn(event("event-1"));
    SecurityContextHolder.getContext().setAuthentication(authentication());

    mockMvc
        .perform(
            put("/api/events/event-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateRequest())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("event-1"));
  }

  @Test
  void deleteEventReturnsNoContent() throws Exception {
    SecurityContextHolder.getContext().setAuthentication(authentication());

    mockMvc.perform(delete("/api/events/event-1")).andExpect(status().isNoContent());

    verify(eventUseCase).deleteById("event-1", "user-1", false);
  }

  @Test
  void invalidPageSizeReturnsStructuredValidationError() throws Exception {
    mockMvc
        .perform(get("/api/events").param("size", "101"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"));
  }

  @Test
  void invalidCategoryReturnsStructuredParameterError() throws Exception {
    mockMvc
        .perform(get("/api/events").param("category", "NOT_A_CATEGORY"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid request parameter"));
  }

  @Test
  void malformedJsonReturnsSafeError() throws Exception {
    SecurityContextHolder.getContext().setAuthentication(authentication());

    mockMvc
        .perform(post("/api/events").contentType(MediaType.APPLICATION_JSON).content("{"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Malformed request body"));
  }

  @Test
  void invalidEventPayloadReturnsValidationErrors() throws Exception {
    EventDtos.CreateEventRequest invalid =
        new EventDtos.CreateEventRequest(
            "", null, "", "", List.of(), null, null, null, false, null, null, null, null, 0,
            List.of(), null);
    SecurityContextHolder.getContext().setAuthentication(authentication());

    mockMvc
        .perform(
            post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"));
  }

  private EventDtos.CreateEventRequest validCreateRequest() {
    return new EventDtos.CreateEventRequest(
        "Stockholm",
        "Desc",
        "Central",
        "Meetup",
        List.of("tech"),
        Category.MEETUP,
        CountryCode.SE,
        "Host",
        false,
        LocalDate.of(2026, 4, 16),
        null,
        LocalTime.of(18, 0),
        LocalTime.of(21, 0),
        20,
        List.of("https://example.com/cover.jpg"),
        new EventDtos.CoordinatesRequest(59.3293, 18.0686));
  }

  private EventDtos.UpdateEventRequest validUpdateRequest() {
    EventDtos.CreateEventRequest request = validCreateRequest();
    return new EventDtos.UpdateEventRequest(
        request.city(),
        request.description(),
        request.address(),
        request.name(),
        request.keywords(),
        request.category(),
        request.countryCode(),
        request.host(),
        request.recurring(),
        request.date(),
        request.weekday(),
        request.startsAt(),
        request.endsAt(),
        request.capacity(),
        request.images(),
        request.coordinates());
  }

  private Event event(String id) {
    return Event.builder()
        .id(id)
        .city("Stockholm")
        .description("Desc")
        .address("Central")
        .name("Meetup")
        .keywords(List.of("tech"))
        .category(Category.MEETUP)
        .countryCode(CountryCode.SE)
        .host("Host")
        .ownerId("user-1")
        .created(LocalDate.now())
        .updated(LocalDate.now())
        .date(LocalDate.of(2026, 4, 16))
        .startsAt(LocalTime.of(18, 0))
        .endsAt(LocalTime.of(21, 0))
        .capacity(20)
        .active(true)
        .images(List.of("https://example.com/cover.jpg"))
        .attendees(List.of("user-1", "user-2"))
        .source(
            new EventSource(
                SourceType.API,
                "community-calendar",
                "external-1",
                "https://example.com/events/1",
                Instant.parse("2026-04-16T10:15:30Z")))
        .coordinates(new Coordinates(59.3293, 18.0686))
        .build();
  }

  private UsernamePasswordAuthenticationToken authentication() {
    UserPrincipal principal = new UserPrincipal(user());
    return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
  }

  private com.vibehopper.domain.user.AppUser user() {
    return new com.vibehopper.domain.user.AppUser(
        "user-1",
        "Ada",
        "ada@example.com",
        "hash",
        com.vibehopper.domain.user.Role.USER,
        false,
        true);
  }
}
