package com.vibehopper.infrastructure.security;

import com.vibehopper.application.user.port.UserRepositoryPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {
  private final UserRepositoryPort users;

  public JpaUserDetailsService(UserRepositoryPort users) {
    this.users = users;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return users
        .findByEmail(username)
        .map(UserPrincipal::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}
