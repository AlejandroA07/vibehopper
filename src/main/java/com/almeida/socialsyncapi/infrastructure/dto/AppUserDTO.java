package com.almeida.socialsyncapi.infrastructure.dto;

import com.almeida.socialsyncapi.domain.enums.Role;

public record AppUserDTO(
        String id,
        String name,
        String email,
        Role role,
        Boolean Locked,
        Boolean enabled
) {}
