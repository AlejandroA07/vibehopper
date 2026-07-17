package com.vibehopper.infrastructure.web;

import com.vibehopper.application.user.usecase.AuthenticationUseCase;
import com.vibehopper.infrastructure.web.dto.AuthDtos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication")
public class AuthController {
  private final AuthenticationUseCase auth;

  public AuthController(AuthenticationUseCase auth) {
    this.auth = auth;
  }

  @PostMapping("/register")
  @Operation(
      operationId = "register",
      summary = "Register a user",
      description = "Requires a valid CSRF cookie/header pair.")
  public ResponseEntity<AuthDtos.AuthResponse> register(
      @Valid @RequestBody AuthDtos.RegisterRequest r) {
    return ResponseEntity.ok(
        new AuthDtos.AuthResponse(auth.register(r.name(), r.email(), r.password())));
  }

  @PostMapping("/login")
  @Operation(
      operationId = "login",
      summary = "Log in",
      description = "Requires a valid CSRF cookie/header pair.")
  public ResponseEntity<AuthDtos.AuthResponse> login(@Valid @RequestBody AuthDtos.LoginRequest r) {
    return ResponseEntity.ok(new AuthDtos.AuthResponse(auth.login(r.email(), r.password())));
  }
}
