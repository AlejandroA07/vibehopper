package com.vibehopper.infrastructure.web;

import com.vibehopper.application.user.usecase.AuthenticationUseCase;
import com.vibehopper.infrastructure.web.dto.AuthDtos;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthenticationUseCase auth;

  public AuthController(AuthenticationUseCase auth) {
    this.auth = auth;
  }

  @PostMapping("/register")
  public ResponseEntity<AuthDtos.AuthResponse> register(
      @Valid @RequestBody AuthDtos.RegisterRequest r) {
    return ResponseEntity.ok(
        new AuthDtos.AuthResponse(auth.register(r.name(), r.email(), r.password())));
  }

  @PostMapping("/login")
  public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest r) {
    return ResponseEntity.ok(new AuthDtos.AuthResponse(auth.login(r.email(), r.password())));
  }
}
