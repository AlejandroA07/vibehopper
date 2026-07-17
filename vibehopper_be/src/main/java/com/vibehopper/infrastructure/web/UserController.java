package com.vibehopper.infrastructure.web;

import com.vibehopper.application.user.usecase.UserUseCase;
import com.vibehopper.infrastructure.security.UserPrincipal;
import com.vibehopper.infrastructure.web.dto.*;
import com.vibehopper.infrastructure.web.mapper.WebMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Users")
public class UserController {
  private static final String BEARER_AUTH = "bearerAuth";
  private final UserUseCase users;

  public UserController(UserUseCase users) {
    this.users = users;
  }

  @GetMapping("/api/me")
  @Operation(
      operationId = "getCurrentUser",
      summary = "Get the current user",
      security = @SecurityRequirement(name = BEARER_AUTH))
  public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserPrincipal p) {
    return ResponseEntity.ok(WebMapper.toResponse(users.findById(p.id())));
  }

  @GetMapping("/api/admin/users/{id}")
  @Operation(
      operationId = "getUserById",
      summary = "Get a user as an administrator",
      security = @SecurityRequirement(name = BEARER_AUTH))
  public ResponseEntity<UserResponse> findById(@PathVariable String id) {
    return ResponseEntity.ok(WebMapper.toResponse(users.findById(id)));
  }

  @GetMapping("/api/admin/users")
  @Operation(
      operationId = "listUsers",
      summary = "List users as an administrator",
      security = @SecurityRequirement(name = BEARER_AUTH))
  public ResponseEntity<List<UserResponse>> findAll() {
    return ResponseEntity.ok(users.findAll().stream().map(WebMapper::toResponse).toList());
  }

  @PatchMapping("/api/admin/users/{id}/role")
  @Operation(
      operationId = "changeUserRole",
      summary = "Change a user's role",
      description = "Requires ADMIN and a valid CSRF cookie/header pair.",
      security = @SecurityRequirement(name = BEARER_AUTH))
  public ResponseEntity<UserResponse> changeRole(
      @PathVariable String id, @Valid @RequestBody ChangeRoleRequest r) {
    return ResponseEntity.ok(WebMapper.toResponse(users.changeRole(id, r.role())));
  }
}
