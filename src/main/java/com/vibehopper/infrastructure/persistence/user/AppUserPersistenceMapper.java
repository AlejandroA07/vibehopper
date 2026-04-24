package com.vibehopper.infrastructure.persistence.user;

import com.vibehopper.domain.user.AppUser;

import java.util.UUID;

final class AppUserPersistenceMapper {
    private AppUserPersistenceMapper() {}

    static AppUser toDomain(AppUserEntity entity) {
        return new AppUser(
                entity.getId().toString(),
                entity.getName(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.isLocked(),
                entity.isEnabled());
    }

    static AppUserEntity toEntity(AppUser user) {
        AppUserEntity entity = new AppUserEntity();
        entity.setId(user.id() == null ? UUID.randomUUID() : UUID.fromString(user.id()));
        entity.setName(user.name());
        entity.setEmail(user.email());
        entity.setPassword(user.password());
        entity.setRole(user.role());
        entity.setLocked(user.locked());
        entity.setEnabled(user.enabled());
        return entity;
    }
}
