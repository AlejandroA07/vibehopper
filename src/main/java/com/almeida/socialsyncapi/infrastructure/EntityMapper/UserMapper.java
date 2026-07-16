package com.almeida.socialsyncapi.infrastructure.EntityMapper;

import static java.util.Objects.requireNonNullElse;

import com.almeida.socialsyncapi.domain.AppUser;
import com.almeida.socialsyncapi.domain.enums.Role;
import com.almeida.socialsyncapi.infrastructure.dto.AppUserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
  public AppUserDTO toDTO(AppUser appUser) {
    return new AppUserDTO(
        appUser.getId(),
        appUser.getName(),
        appUser.getEmail(),
        appUser.getRole(),
        appUser.getLocked(),
        appUser.getEnabled());
  }

  public AppUser toEntity(AppUserDTO appUserDTO) {
    return new AppUser.UserBuilder()
        .setId(appUserDTO.id())
        .setName(appUserDTO.name())
        .setEmail(appUserDTO.email())
        .setRole(requireNonNullElse(appUserDTO.role(), Role.USER))
        .setLocked(requireNonNullElse(appUserDTO.locked(), false))
        .setEnabled(requireNonNullElse(appUserDTO.enabled(), true))
        .build();
  }

  public static AppUser updateUserFromDTO(AppUser existingAppUser, AppUserDTO appUserDTO) {
    return new AppUser.UserBuilder()
        .setId(existingAppUser.getId())
        .setName(requireNonNullElse(appUserDTO.name(), existingAppUser.getName()))
        .setEmail(requireNonNullElse(appUserDTO.email(), existingAppUser.getEmail()))
        .setPassword(existingAppUser.getPassword())
        .setRole(existingAppUser.getRole())
        .setLocked(requireNonNullElse(appUserDTO.locked(), existingAppUser.getLocked()))
        .setEnabled(requireNonNullElse(appUserDTO.enabled(), existingAppUser.getEnabled()))
        .build();
  }
}
