package com.almeida.socialsyncapi.service;

import com.almeida.socialsyncapi.domain.AppUser;
import com.almeida.socialsyncapi.domain.enums.Role;
import com.almeida.socialsyncapi.infrastructure.config.JwtService;
import com.almeida.socialsyncapi.infrastructure.dto.auth.AuthenticationRequest;
import com.almeida.socialsyncapi.infrastructure.dto.auth.AuthenticationResponse;
import com.almeida.socialsyncapi.infrastructure.dto.auth.RegisterRequest;
import com.almeida.socialsyncapi.infrastructure.exception.DuplicateResourceException;
import com.almeida.socialsyncapi.infrastructure.exception.ResourceNotFoundException;
import com.almeida.socialsyncapi.repository.AppUserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  private final AppUserRepository appUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationService(
      AppUserRepository appUserRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      AuthenticationManager authenticationManager) {
    this.appUserRepository = appUserRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  public AuthenticationResponse register(RegisterRequest request) {

    if (appUserRepository.findByEmail(request.getEmail()).isPresent()) {
      throw new DuplicateResourceException("Email already in use");
    }

    var encodedPassword = passwordEncoder.encode(request.getPassword());

    var user =
        AppUser.builder()
            .setName(request.getName())
            .setEmail(request.getEmail())
            .setPassword(encodedPassword)
            .setRole(Role.USER)
            .setLocked(false)
            .setEnabled(true)
            .build();
    appUserRepository.save(user);
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    var user =
        appUserRepository
            .findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    var jwtToken = jwtService.generateToken(user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }
}
