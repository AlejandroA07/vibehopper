package com.almeida.socialsyncapi.repository;

import com.almeida.socialsyncapi.domain.AppUser;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends MongoRepository<AppUser, String> {

  Optional<AppUser> findByEmail(String email);
}
