package com.vibehopper.infrastructure.web.dto;

import com.vibehopper.domain.user.Role;

public record UserResponse(
    String id, String name, String email, Role role, boolean locked, boolean enabled) {}
