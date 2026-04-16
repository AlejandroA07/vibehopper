package com.vibehopper.infrastructure.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibehopper.application.common.NotFoundException;
import com.vibehopper.application.event.usecase.EventUseCase;
import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.event.CountryCode;
import com.vibehopper.domain.event.Event;
import com.vibehopper.infrastructure.security.UserPrincipal;
import com.vibehopper.infrastructure.security.JwtService;
import com.vibehopper.infrastructure.web.dto.EventDtos;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventControllerTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private JwtService jwtService;
    @MockBean private EventUseCase eventUseCase;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void listEventsReturnsEvents() throws Exception {
        when(eventUseCase.findAll()).thenReturn(List.of(event("event-1")));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("event-1"));
    }

    @Test
    void getEventReturnsNotFoundErrorShape() throws Exception {
        when(eventUseCase.findById("missing")).thenThrow(new NotFoundException("Event not found"));

        mockMvc.perform(get("/api/events/missing"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Event not found"));
    }

    @Test
    void createEventUsesAuthenticatedPrincipal() throws Exception {
        when(eventUseCase.create(any(Event.class), eq("user-1"))).thenReturn(event("event-1"));

        SecurityContextHolder.getContext().setAuthentication(authentication());

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("event-1"));
    }

    @Test
    void updateEventUsesPathId() throws Exception {
        when(eventUseCase.update(eq("event-1"), any(Event.class))).thenReturn(event("event-1"));

        mockMvc.perform(put("/api/events/event-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("event-1"));
    }

    @Test
    void deleteEventReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/events/event-1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void searchSupportsCityCategoryDateAndGeoParams() throws Exception {
        when(eventUseCase.search(any())).thenReturn(List.of(event("event-1")));

        mockMvc.perform(get("/api/events/search")
                        .param("city", "Stockholm")
                        .param("category", "MEETUP")
                        .param("from", "2026-04-16")
                        .param("to", "2026-04-17")
                        .param("lat", "59.3")
                        .param("lng", "18.0")
                        .param("radiusKm", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("event-1"));

        verify(eventUseCase, never()).findById("search");
    }

    @Test
    void invalidEventPayloadReturnsValidationErrors() throws Exception {
        EventDtos.EventRequest invalid = new EventDtos.EventRequest("", null, "", "", List.of(), null, null, null,
                false, null, null, null, null, 0, List.of(), null, null);

        SecurityContextHolder.getContext().setAuthentication(authentication());

        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    private EventDtos.EventRequest validRequest() {
        return new EventDtos.EventRequest("Stockholm", "Desc", "Central", "Meetup", List.of("tech"), Category.MEETUP,
                CountryCode.SE, "Host", false, LocalDate.of(2026, 4, 16), null, null, null, 20, List.of(), null, null);
    }

    private Event event(String id) {
        return new Event(id, "Stockholm", "Desc", "Central", "Meetup", List.of("tech"), Category.MEETUP,
                CountryCode.SE, "Host", "user-1", LocalDate.now(), LocalDate.now(), false, LocalDate.of(2026, 4, 16),
                null, null, null, 20, false, true, false, List.of(), List.of(), null, null);
    }

    private UsernamePasswordAuthenticationToken authentication() {
        UserPrincipal principal = new UserPrincipal(user());
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    private com.vibehopper.domain.user.AppUser user() {
        return new com.vibehopper.domain.user.AppUser("user-1", "Ada", "ada@example.com", "hash", com.vibehopper.domain.user.Role.USER, false, true);
    }
}
