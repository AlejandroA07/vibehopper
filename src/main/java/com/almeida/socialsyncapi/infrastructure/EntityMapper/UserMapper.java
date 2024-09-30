package com.almeida.socialsyncapi.infrastructure.EntityMapper;

import com.almeida.socialsyncapi.domain.AppUser;
import com.almeida.socialsyncapi.infrastructure.dto.AppUserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public AppUserDTO toDTO(AppUser appUser) {
        return new AppUserDTO(
                appUser.getId(),
                appUser.getName(),
                appUser.getEmail()
        );
    }


    public AppUser toEntity(AppUserDTO appUserDTO) {
        return new AppUser.UserBuilder()
                .setId(appUserDTO.id())
                .setName(appUserDTO.name())
                .setEmail(appUserDTO.email())
                .build();
    }

    public static AppUser updateUserFromDTO(AppUser existingAppUser, AppUserDTO appUserDTO) {
        return new AppUser.UserBuilder()
                .setId(existingAppUser.getId())
                .setName(appUserDTO.name() != null ? appUserDTO.name() : existingAppUser.getName())
                .setEmail(appUserDTO.email() != null ? appUserDTO.email() : existingAppUser.getEmail())
                .setPassword(existingAppUser.getPassword())
                .build();
    }
}
