package com.vibehopper.infrastructure.web.dto;

import com.vibehopper.domain.user.Role;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(@NotNull Role role) {}
