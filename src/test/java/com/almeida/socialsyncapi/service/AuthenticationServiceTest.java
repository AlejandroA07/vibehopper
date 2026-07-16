package com.almeida.socialsyncapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.almeida.socialsyncapi.domain.AppUser;
import com.almeida.socialsyncapi.domain.enums.Role;
import com.almeida.socialsyncapi.infrastructure.config.JwtService;
import com.almeida.socialsyncapi.infrastructure.dto.auth.RegisterRequest;
import com.almeida.socialsyncapi.infrastructure.exception.DuplicateResourceException;
import com.almeida.socialsyncapi.repository.AppUserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

  @Mock private AppUserRepository appUserRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtService jwtService;

  @Mock private AuthenticationManager authenticationManager;

  @InjectMocks private AuthenticationService authenticationService;

  @Test
  void registerHashesPasswordAndSetsAccountDefaults() {
    RegisterRequest request = new RegisterRequest("Manuel", "manuel@example.com", "password123");
    when(appUserRepository.findByEmail("manuel@example.com")).thenReturn(Optional.empty());
    when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
    when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

    var response = authenticationService.register(request);

    ArgumentCaptor<AppUser> userCaptor = ArgumentCaptor.forClass(AppUser.class);
    verify(appUserRepository).save(userCaptor.capture());
    AppUser savedUser = userCaptor.getValue();

    assertThat(response.getToken()).isEqualTo("jwt-token");
    assertThat(savedUser.getName()).isEqualTo("Manuel");
    assertThat(savedUser.getEmail()).isEqualTo("manuel@example.com");
    assertThat(savedUser.getPassword()).isEqualTo("hashed-password");
    assertThat(savedUser.getPassword()).isNotEqualTo("password123");
    assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    assertThat(savedUser.getLocked()).isFalse();
    assertThat(savedUser.getEnabled()).isTrue();
    assertThat(savedUser.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
  }

  @Test
  void registerRejectsDuplicateEmail() {
    RegisterRequest request = new RegisterRequest("Manuel", "manuel@example.com", "password123");
    when(appUserRepository.findByEmail("manuel@example.com"))
        .thenReturn(Optional.of(AppUser.builder().setEmail("manuel@example.com").build()));

    assertThatThrownBy(() -> authenticationService.register(request))
        .isInstanceOf(DuplicateResourceException.class)
        .hasMessage("Email already in use");

    verify(appUserRepository, never()).save(any(AppUser.class));
  }
}
