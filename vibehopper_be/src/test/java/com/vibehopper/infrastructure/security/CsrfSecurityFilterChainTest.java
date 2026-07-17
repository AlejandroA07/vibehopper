package com.vibehopper.infrastructure.security;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibehopper.application.user.usecase.AuthenticationUseCase;
import com.vibehopper.infrastructure.config.CorsProperties;
import com.vibehopper.infrastructure.web.AuthController;
import com.vibehopper.infrastructure.web.CsrfController;
import com.vibehopper.infrastructure.web.dto.AuthDtos;
import jakarta.servlet.http.Cookie;
import java.util.Objects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(
    controllers = {AuthController.class, CsrfController.class},
    excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@Import({
  SecurityConfiguration.class,
  JwtAuthenticationFilter.class,
  RestAuthenticationEntryPoint.class,
  RestAccessDeniedHandler.class
})
class CsrfSecurityFilterChainTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockitoBean private AuthenticationUseCase authenticationUseCase;
  @MockitoBean private JwtService jwtService;
  @MockitoBean private UserDetailsService userDetailsService;
  @MockitoBean private CorsProperties corsProperties;

  @Test
  void csrfTokenEndpointIsPublicAndIssuesStrictHttpOnlyCookie() throws Exception {
    mockMvc
        .perform(get("/api/csrf"))
        .andExpect(status().isOk())
        .andExpect(cookie().exists("XSRF-TOKEN"))
        .andExpect(cookie().httpOnly("XSRF-TOKEN", true))
        .andExpect(cookie().sameSite("XSRF-TOKEN", "Strict"))
        .andExpect(jsonPath("$.headerName").value("X-XSRF-TOKEN"))
        .andExpect(jsonPath("$.token").isNotEmpty());
  }

  @Test
  void issuedCookieAndTokenAuthorizeUnsafeRequest() throws Exception {
    MvcResult csrfResult = mockMvc.perform(get("/api/csrf")).andExpect(status().isOk()).andReturn();
    Cookie csrfCookie = Objects.requireNonNull(csrfResult.getResponse().getCookie("XSRF-TOKEN"));
    String csrfToken =
        objectMapper.readTree(csrfResult.getResponse().getContentAsString()).get("token").asText();
    AuthDtos.RegisterRequest request =
        new AuthDtos.RegisterRequest("Ada", "ada@example.com", "password123");
    when(authenticationUseCase.register("Ada", "ada@example.com", "password123"))
        .thenReturn("jwt-token");

    mockMvc
        .perform(
            post("/api/auth/register")
                .cookie(csrfCookie)
                .header("X-XSRF-TOKEN", csrfToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("jwt-token"));
  }
}
