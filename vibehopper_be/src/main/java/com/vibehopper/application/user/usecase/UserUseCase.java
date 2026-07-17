package com.vibehopper.application.user.usecase;

import com.vibehopper.application.common.NotFoundException;
import com.vibehopper.application.user.port.UserRepositoryPort;
import com.vibehopper.domain.user.AppUser;
import com.vibehopper.domain.user.Role;
import java.util.List;

public final class UserUseCase {
  private final UserRepositoryPort users;

  public UserUseCase(UserRepositoryPort users) {
    this.users = users;
  }

  public AppUser findById(String id) {
    return users.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
  }

  public AppUser findByEmail(String email) {
    return users.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
  }

  public List<AppUser> findAll() {
    return users.findAll();
  }

  public AppUser changeRole(String id, Role role) {
    return users.save(findById(id).withRole(role));
  }
}
