package com.vibehopper.infrastructure.web;

import com.vibehopper.application.user.usecase.UserUseCase;
import com.vibehopper.infrastructure.security.UserPrincipal;
import com.vibehopper.infrastructure.web.dto.*;
import com.vibehopper.infrastructure.web.mapper.WebMapper;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
  private final UserUseCase users;

  public UserController(UserUseCase users) {
    this.users = users;
  }

  @GetMapping("/api/me")
  public ResponseEntity<UserResponse> me(@AuthenticationPrincipal UserPrincipal p) {
    return ResponseEntity.ok(WebMapper.toResponse(users.findById(p.id())));
  }

  @GetMapping("/api/admin/users/{id}")
  public ResponseEntity<UserResponse> findById(@PathVariable String id) {
    return ResponseEntity.ok(WebMapper.toResponse(users.findById(id)));
  }

  @GetMapping("/api/admin/users")
  public ResponseEntity<List<UserResponse>> findAll() {
    return ResponseEntity.ok(users.findAll().stream().map(WebMapper::toResponse).toList());
  }

  @PatchMapping("/api/admin/users/{id}/role")
  public ResponseEntity<UserResponse> changeRole(
      @PathVariable String id, @Valid @RequestBody ChangeRoleRequest r) {
    return ResponseEntity.ok(WebMapper.toResponse(users.changeRole(id, r.role())));
  }
}
