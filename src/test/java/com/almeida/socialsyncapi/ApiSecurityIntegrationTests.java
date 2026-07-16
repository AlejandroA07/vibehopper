package com.almeida.socialsyncapi;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.almeida.socialsyncapi.domain.AppUser;
import com.almeida.socialsyncapi.domain.Event;
import com.almeida.socialsyncapi.domain.enums.Role;
import com.almeida.socialsyncapi.repository.AppUserRepository;
import com.almeida.socialsyncapi.repository.EventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(
    properties = {
      "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration"
    })
@AutoConfigureMockMvc
class ApiSecurityIntegrationTests {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private AppUserRepository appUserRepository;

  @MockBean private EventRepository eventRepository;

  private AppUser owner;
  private AppUser otherUser;
  private AppUser admin;

  @DynamicPropertySource
  static void jwtProperties(DynamicPropertyRegistry registry) {
    registry.add("security.jwt.secret", () -> TestJwtSecret.VALUE);
  }

  @BeforeEach
  void setUp() {
    owner = appUser("owner-1", "owner@example.com", Role.USER);
    otherUser = appUser("other-1", "other@example.com", Role.USER);
    admin = appUser("admin-1", "admin@example.com", Role.ADMIN);
  }

  @Test
  void anonymousCanReadEventsButCannotCreateEvents() throws Exception {
    when(eventRepository.findAll()).thenReturn(List.of());

    mockMvc.perform(get("/api/event/events")).andExpect(status().isOk());

    mockMvc
        .perform(
            post("/api/event/insert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("name", "Meetup"))))
        .andExpect(status().isForbidden());
  }

  @Test
  void userCanCreateEventAndBecomesOwner() throws Exception {
    when(eventRepository.save(any(Event.class)))
        .thenAnswer(
            invocation -> {
              Event event = invocation.getArgument(0);
              event.setEventId("event-1");
              return event;
            });

    mockMvc
        .perform(
            post("/api/event/insert")
                .with(user(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json(
                        Map.of(
                            "name", "Board game night",
                            "city", "Stockholm",
                            "ownerId", "client-controlled",
                            "capacity", 12))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.eventId").value("event-1"))
        .andExpect(jsonPath("$.ownerId").value("owner-1"));

    ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
    verify(eventRepository).save(eventCaptor.capture());
    Event savedEvent = eventCaptor.getValue();

    org.assertj.core.api.Assertions.assertThat(savedEvent.getOwnerId()).isEqualTo("owner-1");
    org.assertj.core.api.Assertions.assertThat(savedEvent.isActive()).isTrue();
    org.assertj.core.api.Assertions.assertThat(savedEvent.getCreated()).isNotNull();
    org.assertj.core.api.Assertions.assertThat(savedEvent.getUpdated()).isNotNull();
  }

  @Test
  void userCannotUpdateAnotherUsersEvent() throws Exception {
    when(eventRepository.findById("event-1")).thenReturn(Optional.of(event("event-1", otherUser)));

    mockMvc
        .perform(
            put("/api/event/edit/event-1")
                .with(user(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("name", "Updated"))))
        .andExpect(status().isForbidden());

    verify(eventRepository, never()).save(any(Event.class));
  }

  @Test
  void adminCanChangeRolesAndManageAnyEvent() throws Exception {
    when(appUserRepository.findById("owner-1")).thenReturn(Optional.of(owner));
    when(appUserRepository.save(any(AppUser.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc
        .perform(put("/api/user/changeRole/owner-1").with(user(admin)).param("role", "ADMIN"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value("ADMIN"));

    when(eventRepository.findById("event-1")).thenReturn(Optional.of(event("event-1", otherUser)));
    when(eventRepository.save(any(Event.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc
        .perform(
            put("/api/event/edit/event-1")
                .with(user(admin))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json(Map.of("name", "Admin update"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Admin update"))
        .andExpect(jsonPath("$.ownerId").value("other-1"));
  }

  @Test
  void eventUpdateUsesPathIdNotBodyId() throws Exception {
    when(eventRepository.findById("path-id")).thenReturn(Optional.of(event("path-id", owner)));
    when(eventRepository.save(any(Event.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    mockMvc
        .perform(
            put("/api/event/edit/path-id")
                .with(user(owner))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json(
                        Map.of(
                            "eventId", "body-id",
                            "name", "Path wins"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.eventId").value("path-id"))
        .andExpect(jsonPath("$.name").value("Path wins"));

    verify(eventRepository).findById("path-id");
    verify(eventRepository, never()).findById("body-id");
  }

  @Test
  void malformedRegisterRequestReturnsBadRequest() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json(
                        Map.of(
                            "name", "",
                            "email", "not-an-email",
                            "password", "short"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.fields.email").exists())
        .andExpect(jsonPath("$.fields.password").exists());
  }

  @Test
  void duplicateRegistrationReturnsConflict() throws Exception {
    when(appUserRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    json(
                        Map.of(
                            "name", "Owner",
                            "email", "owner@example.com",
                            "password", "password123"))))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Email already in use"));
  }

  @Test
  void userCanReadSelfButNotUserAdministration() throws Exception {
    mockMvc
        .perform(get("/api/user/me").with(user(owner)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("owner-1"))
        .andExpect(jsonPath("$.email").value("owner@example.com"));

    mockMvc.perform(get("/api/user/users").with(user(owner))).andExpect(status().isForbidden());
  }

  private String json(Object value) throws Exception {
    return objectMapper.writeValueAsString(value);
  }

  private AppUser appUser(String id, String email, Role role) {
    return AppUser.builder()
        .setId(id)
        .setName(email.substring(0, email.indexOf('@')))
        .setEmail(email)
        .setPassword("encoded-password")
        .setRole(role)
        .setLocked(false)
        .setEnabled(true)
        .build();
  }

  private Event event(String id, AppUser owner) {
    return Event.builder()
        .eventId(id)
        .name("Original")
        .ownerId(owner.getId())
        .active(true)
        .capacity(10)
        .build();
  }
}
