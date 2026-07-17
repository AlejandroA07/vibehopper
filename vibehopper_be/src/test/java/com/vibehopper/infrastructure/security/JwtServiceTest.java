package com.vibehopper.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import com.vibehopper.domain.user.AppUser;
import com.vibehopper.domain.user.Role;
import com.vibehopper.infrastructure.config.JwtProperties;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
  private static final String TEST_KEY = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=";
  private final JwtService jwtService =
      new JwtService(new JwtProperties(TEST_KEY, Duration.ofHours(1)));

  @Test
  void validTokenIsAcceptedForEnabledUnlockedAccount() {
    AppUser user = user(false, true);
    String token = jwtService.issueToken(user);

    assertThat(jwtService.isTokenValid(token, new UserPrincipal(user))).isTrue();
  }

  @Test
  void validTokenIsRejectedWhenAccountIsLocked() {
    AppUser user = user(true, true);
    String token = jwtService.issueToken(user);

    assertThat(jwtService.isTokenValid(token, new UserPrincipal(user))).isFalse();
  }

  @Test
  void validTokenIsRejectedWhenAccountIsDisabled() {
    AppUser user = user(false, false);
    String token = jwtService.issueToken(user);

    assertThat(jwtService.isTokenValid(token, new UserPrincipal(user))).isFalse();
  }

  private AppUser user(boolean locked, boolean enabled) {
    return new AppUser("user-1", "Ada", "ada@example.com", "hash", Role.USER, locked, enabled);
  }
}
