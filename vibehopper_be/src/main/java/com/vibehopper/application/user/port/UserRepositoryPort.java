package com.vibehopper.application.user.port;

import com.vibehopper.domain.user.AppUser;
import java.util.List;
import java.util.Optional;

public interface UserRepositoryPort {
  AppUser save(AppUser user);

  Optional<AppUser> findById(String id);

  Optional<AppUser> findByEmail(String email);

  List<AppUser> findAll();
}
