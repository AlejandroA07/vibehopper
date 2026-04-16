package com.vibehopper.infrastructure.persistence.user;
import com.vibehopper.application.user.port.UserRepositoryPort; import com.vibehopper.domain.user.AppUser; import org.springframework.stereotype.Repository; import java.util.*;
@Repository public class AppUserPersistenceAdapter implements UserRepositoryPort {
    private final SpringDataAppUserRepository repository; public AppUserPersistenceAdapter(SpringDataAppUserRepository repository){this.repository=repository;}
    public AppUser save(AppUser user){ return AppUserPersistenceMapper.toDomain(repository.save(AppUserPersistenceMapper.toDocument(user))); }
    public Optional<AppUser> findById(String id){ return repository.findById(id).map(AppUserPersistenceMapper::toDomain); }
    public Optional<AppUser> findByEmail(String email){ return repository.findByEmail(email).map(AppUserPersistenceMapper::toDomain); }
    public List<AppUser> findAll(){ return repository.findAll().stream().map(AppUserPersistenceMapper::toDomain).toList(); }
}
