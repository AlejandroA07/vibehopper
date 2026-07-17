package com.vibehopper.infrastructure.web;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibehopper.application.common.ConflictException;
import com.vibehopper.application.user.usecase.AuthenticationUseCase;
import com.vibehopper.infrastructure.security.JwtService;
import com.vibehopper.infrastructure.web.dto.AuthDtos;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = AuthController.class,
    excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private JwtService jwtService;
  @MockBean private UserDetailsService userDetailsService;
  @MockBean private AuthenticationUseCase authenticationUseCase;

  @Test
  void registerReturnsToken() throws Exception {
    when(authenticationUseCase.register("Ada", "ada@example.com", "password123"))
        .thenReturn("jwt-token");

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new AuthDtos.RegisterRequest("Ada", "ada@example.com", "password123"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token"));
  }

  @Test
  void loginReturnsToken() throws Exception {
    when(authenticationUseCase.login("ada@example.com", "password123")).thenReturn("jwt-token");

    mockMvc
        .perform(
            post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new AuthDtos.LoginRequest("ada@example.com", "password123"))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token"));
  }

  @Test
  void invalidRegisterPayloadReturnsValidationErrorShape() throws Exception {
    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new AuthDtos.RegisterRequest("", "bad-email", "short"))))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.fieldErrors").isArray());
  }

  @Test
  void duplicateEmailReturnsConflictErrorShape() throws Exception {
    when(authenticationUseCase.register(anyString(), anyString(), anyString()))
        .thenThrow(new ConflictException("Email already in use"));

    mockMvc
        .perform(
            post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    objectMapper.writeValueAsString(
                        new AuthDtos.RegisterRequest("Ada", "ada@example.com", "password123"))))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("Email already in use"));
  }
}
