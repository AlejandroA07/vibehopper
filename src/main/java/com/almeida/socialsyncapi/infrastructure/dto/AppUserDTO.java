package com.almeida.socialsyncapi.infrastructure.dto;

import com.almeida.socialsyncapi.domain.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record AppUserDTO(
    String id,
    @Size(max = 120) String name,
    @Email @Size(max = 254) String email,
    Role role,
    Boolean locked,
    Boolean enabled) {}
