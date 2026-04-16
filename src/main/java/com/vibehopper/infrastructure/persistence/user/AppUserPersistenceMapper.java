package com.vibehopper.infrastructure.persistence.user;
import com.vibehopper.domain.user.AppUser;
final class AppUserPersistenceMapper { private AppUserPersistenceMapper(){} static AppUser toDomain(AppUserDocument d){ return new AppUser(d.getId(),d.getName(),d.getEmail(),d.getPassword(),d.getRole(),d.isLocked(),d.isEnabled()); } static AppUserDocument toDocument(AppUser u){ AppUserDocument d=new AppUserDocument(); d.setId(u.id()); d.setName(u.name()); d.setEmail(u.email()); d.setPassword(u.password()); d.setRole(u.role()); d.setLocked(u.locked()); d.setEnabled(u.enabled()); return d; } }
