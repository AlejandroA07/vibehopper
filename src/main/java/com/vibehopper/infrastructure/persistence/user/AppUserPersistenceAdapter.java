package com.vibehopper.infrastructure.persistence.user;

import com.vibehopper.application.common.ConflictException;
import com.vibehopper.application.user.port.UserRepositoryPort;
import com.vibehopper.domain.user.AppUser;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AppUserPersistenceAdapter implements UserRepositoryPort {
    private final AppUserJpaRepository repository;

    public AppUserPersistenceAdapter(AppUserJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public AppUser save(AppUser user) {
        try {
            return AppUserPersistenceMapper.toDomain(repository.saveAndFlush(AppUserPersistenceMapper.toEntity(user)));
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Email already in use");
        }
    }

    @Override
    public Optional<AppUser> findById(String id) {
        return toUuid(id).flatMap(repository::findById).map(AppUserPersistenceMapper::toDomain);
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email).map(AppUserPersistenceMapper::toDomain);
    }

    @Override
    public List<AppUser> findAll() {
        return repository.findAll().stream().map(AppUserPersistenceMapper::toDomain).toList();
    }

    private Optional<UUID> toUuid(String id) {
        try {
            return Optional.of(UUID.fromString(id));
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }
}
