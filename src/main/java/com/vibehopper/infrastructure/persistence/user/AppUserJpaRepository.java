package com.vibehopper.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AppUserJpaRepository extends JpaRepository<AppUserEntity, UUID> {
    @Query("select u from AppUserEntity u where lower(u.email) = lower(:email)")
    Optional<AppUserEntity> findByEmailIgnoreCase(@Param("email") String email);
}
