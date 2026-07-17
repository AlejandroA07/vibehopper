package com.vibehopper.infrastructure.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibehopper.application.event.usecase.EventUseCase;
import com.vibehopper.application.user.usecase.AuthenticationUseCase;
import com.vibehopper.application.user.usecase.UserUseCase;
import com.vibehopper.domain.event.Category;
import com.vibehopper.domain.event.CountryCode;
import com.vibehopper.domain.event.Event;
import com.vibehopper.domain.user.AppUser;
import com.vibehopper.domain.user.Role;
import com.vibehopper.infrastructure.config.CorsProperties;
import com.vibehopper.infrastructure.web.AuthController;
import com.vibehopper.infrastructure.web.EventController;
import com.vibehopper.infrastructure.web.UserController;
import com.vibehopper.infrastructure.web.dto.AuthDtos;
import com.vibehopper.infrastructure.web.dto.EventDtos;
import io.jsonwebtoken.MalformedJwtException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = {AuthController.class, EventController.class, UserController.class},
    excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@Import({
  SecurityConfiguration.class,
  JwtAuthenticationFilter.class,
  RestAuthenticationEntryPoint.class,
  RestAccessDeniedHandler.class
})
class SecurityFilterChainTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private AuthenticationUseCase authenticationUseCase;
  @MockBean private EventUseCase eventUseCase;
  @MockBean private UserUseCase userUseCase;
  @MockBean private JwtService jwtService;
  @MockBean private UserDetailsService userDetailsService;
  @MockBean private CorsProperties corsProperties;

  @Test
  void anonymousUserCanReadEvents() throws Exception {
    when(eventUseCase.findAll()).thenReturn(List.of());

    mockMvc.perform(get("/api/events")).andExpect(status().isOk());
  }

  @Test
  void anonymousUserCannotWriteEvents() throws Exception {
    mockMvc
        .perform(
            post("/api/events")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Authentication is required"));
  }

  @Test
  void unsafeRequestWithoutCsrfTokenIsForbidden() throws Exception {
    mockMvc
        .perform(
            post("/api/events")
                .with(authentication(principalAuthentication(Role.USER, false, true)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEventRequest())))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }

  @Test
  void registrationRequiresCsrfToken() throws Exception {
    AuthDtos.RegisterRequest request =
        new AuthDtos.RegisterRequest("Ada", "ada@example.com", "password123");

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }

  @Test
  void registrationWithCsrfTokenSucceeds() throws Exception {
    AuthDtos.RegisterRequest request =
        new AuthDtos.RegisterRequest("Ada", "ada@example.com", "password123");
    when(authenticationUseCase.register("Ada", "ada@example.com", "password123"))
        .thenReturn("jwt-token");

    mockMvc
        .perform(
            post("/api/auth/register")
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token"));
  }

  @Test
  void userCanCreateEventAndAuthenticatedPrincipalBecomesOwner() throws Exception {
    when(eventUseCase.create(any(Event.class), eq("user-1"))).thenReturn(event("event-1"));

    mockMvc
        .perform(
            post("/api/events")
                .with(authentication(principalAuthentication(Role.USER, false, true)))
                .with(csrf().asHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validEventRequest())))
        .andExpect(status().isCreated());

    verify(eventUseCase).create(any(Event.class), eq("user-1"));
  }

  @Test
  void userCannotAccessAdminRoutes() throws Exception {
    mockMvc
        .perform(get("/api/admin/users").with(user("ada@example.com").roles("USER")))
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.message").value("Access is denied"));
  }

  @Test
  void adminCanReadIndividualUsers() throws Exception {
    when(userUseCase.findById("user-1")).thenReturn(appUser(Role.USER, false, true));

    mockMvc
        .perform(get("/api/admin/users/user-1").with(user("admin@example.com").roles("ADMIN")))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("user-1"));
  }

  @Test
  void formerPublicUserRouteIsNotAvailable() throws Exception {
    mockMvc
        .perform(
            get("/api/users/user-1")
                .with(authentication(principalAuthentication(Role.USER, false, true))))
        .andExpect(status().isNotFound());
  }

  @Test
  void meRequiresAuthentication() throws Exception {
    mockMvc
        .perform(get("/api/me"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.status").value(401));
  }

  @Test
  void validBearerTokenAuthenticatesCurrentUser() throws Exception {
    UserPrincipal principal = new UserPrincipal(appUser(Role.USER, false, true));
    when(jwtService.extractUsername("valid-token")).thenReturn("ada@example.com");
    when(userDetailsService.loadUserByUsername("ada@example.com")).thenReturn(principal);
    when(jwtService.isTokenValid("valid-token", principal)).thenReturn(true);
    when(userUseCase.findById("user-1")).thenReturn(principal.user());

    mockMvc
        .perform(get("/api/me").header("Authorization", "Bearer valid-token"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("ada@example.com"));
  }

  @Test
  void malformedBearerTokenReturnsStructuredUnauthorizedResponse() throws Exception {
    when(jwtService.extractUsername("bad-token"))
        .thenThrow(new MalformedJwtException("Malformed token"));

    mockMvc
        .perform(get("/api/me").header("Authorization", "Bearer bad-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Authentication is required"))
        .andExpect(jsonPath("$.path").value("/api/me"));
  }

  @Test
  void tokenForUnknownUserReturnsStructuredUnauthorizedResponse() throws Exception {
    when(jwtService.extractUsername("orphan-token")).thenReturn("missing@example.com");
    when(userDetailsService.loadUserByUsername("missing@example.com"))
        .thenThrow(new UsernameNotFoundException("User not found"));

    mockMvc
        .perform(get("/api/me").header("Authorization", "Bearer orphan-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Authentication is required"));
  }

  @Test
  void rejectedAccountTokenReturnsStructuredUnauthorizedResponse() throws Exception {
    UserPrincipal lockedPrincipal = new UserPrincipal(appUser(Role.USER, true, true));
    when(jwtService.extractUsername("locked-token")).thenReturn("ada@example.com");
    when(userDetailsService.loadUserByUsername("ada@example.com")).thenReturn(lockedPrincipal);
    when(jwtService.isTokenValid("locked-token", lockedPrincipal)).thenReturn(false);

    mockMvc
        .perform(get("/api/me").header("Authorization", "Bearer locked-token"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Authentication is required"));
  }

  private EventDtos.EventRequest validEventRequest() {
    return new EventDtos.EventRequest(
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
        null,
        null,
        20,
        List.of(),
        null,
        null);
  }

  private Event event(String id) {
    return Event.builder()
        .id(id)
        .city("Stockholm")
        .address("Central")
        .name("Meetup")
        .keywords(List.of())
        .category(Category.MEETUP)
        .countryCode(CountryCode.SE)
        .ownerId("user-1")
        .created(LocalDate.now())
        .updated(LocalDate.now())
        .date(LocalDate.of(2026, 4, 16))
        .capacity(20)
        .active(true)
        .images(List.of())
        .build();
  }

  private UsernamePasswordAuthenticationToken principalAuthentication(
      Role role, boolean locked, boolean enabled) {
    UserPrincipal principal = new UserPrincipal(appUser(role, locked, enabled));
    return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
  }

  private AppUser appUser(Role role, boolean locked, boolean enabled) {
    return new AppUser("user-1", "Ada", "ada@example.com", "hash", role, locked, enabled);
  }
}
