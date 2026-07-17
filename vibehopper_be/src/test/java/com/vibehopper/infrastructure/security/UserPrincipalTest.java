package com.vibehopper.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.vibehopper.domain.user.*;
import org.junit.jupiter.api.Test;

class UserPrincipalTest {
  @Test
  void roleAuthorityUsesSpringRolePrefix() {
    UserPrincipal p =
        new UserPrincipal(
            new AppUser("1", "Ada", "ada@example.com", "secret", Role.ADMIN, false, true));
    assertThat(p.getAuthorities()).extracting(Object::toString).containsExactly("ROLE_ADMIN");
  }

  @Test
  void userAuthorityUsesSpringRolePrefix() {
    UserPrincipal p =
        new UserPrincipal(
            new AppUser("1", "Ada", "ada@example.com", "secret", Role.USER, false, true));
    assertThat(p.getAuthorities()).extracting(Object::toString).containsExactly("ROLE_USER");
  }
}
