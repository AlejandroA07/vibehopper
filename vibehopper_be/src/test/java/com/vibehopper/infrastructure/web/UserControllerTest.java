package com.vibehopper.infrastructure.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vibehopper.application.user.usecase.UserUseCase;
import com.vibehopper.domain.user.AppUser;
import com.vibehopper.domain.user.Role;
import com.vibehopper.infrastructure.security.JwtService;
import com.vibehopper.infrastructure.security.UserPrincipal;
import com.vibehopper.infrastructure.web.dto.ChangeRoleRequest;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = UserController.class,
    excludeAutoConfiguration = UserDetailsServiceAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @MockBean private JwtService jwtService;
  @MockBean private UserDetailsService userDetailsService;
  @MockBean private UserUseCase userUseCase;

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void meReturnsAuthenticatedUser() throws Exception {
    when(userUseCase.findById("user-1")).thenReturn(user(Role.USER));

    SecurityContextHolder.getContext().setAuthentication(authentication(Role.USER));

    mockMvc
        .perform(get("/api/me"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.email").value("ada@example.com"));
  }

  @Test
  void getUserByIdReturnsUser() throws Exception {
    when(userUseCase.findById("user-1")).thenReturn(user(Role.USER));

    mockMvc
        .perform(get("/api/users/user-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("user-1"));
  }

  @Test
  void adminUsersReturnsUsers() throws Exception {
    when(userUseCase.findAll()).thenReturn(List.of(user(Role.USER), user(Role.ADMIN)));

    mockMvc
        .perform(get("/api/admin/users"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[1].role").value("ADMIN"));
  }

  @Test
  void changeRoleReturnsUpdatedUser() throws Exception {
    when(userUseCase.changeRole("user-1", Role.ADMIN)).thenReturn(user(Role.ADMIN));

    mockMvc
        .perform(
            patch("/api/admin/users/user-1/role")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ChangeRoleRequest(Role.ADMIN))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.role").value("ADMIN"));
  }

  private UsernamePasswordAuthenticationToken authentication(Role role) {
    UserPrincipal principal = new UserPrincipal(user(role));
    return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
  }

  private AppUser user(Role role) {
    return new AppUser("user-1", "Ada", "ada@example.com", "hash", role, false, true);
  }
}
