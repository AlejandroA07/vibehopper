package com.vibehopper.infrastructure.persistence.user;
import org.springframework.data.mongodb.repository.MongoRepository; import java.util.Optional;
public interface SpringDataAppUserRepository extends MongoRepository<AppUserDocument,String> { Optional<AppUserDocument> findByEmail(String email); }
