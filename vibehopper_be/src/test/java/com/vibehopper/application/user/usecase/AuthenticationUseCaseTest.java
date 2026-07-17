package com.vibehopper.application.user.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vibehopper.application.common.ConflictException;
import com.vibehopper.application.user.port.UserRepositoryPort;
import com.vibehopper.domain.user.AppUser;
import com.vibehopper.domain.user.Role;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AuthenticationUseCaseTest {
  @Test
  void registerHashesPasswordAndReturnsToken() {
    FakeUsers users = new FakeUsers(null);
    AuthenticationUseCase useCase =
        new AuthenticationUseCase(
            users, raw -> "hashed-" + raw, (e, p) -> {}, user -> "token-for-" + user.email());

    String token = useCase.register("Ada", "ada@example.com", "password123");

    assertThat(token).isEqualTo("token-for-ada@example.com");
    assertThat(users.saved.password()).isEqualTo("hashed-password123");
    assertThat(users.saved.role()).isEqualTo(Role.USER);
  }

  @Test
  void duplicateEmailReturnsConflict() {
    FakeUsers users =
        new FakeUsers(new AppUser("1", "Ada", "ada@example.com", "hash", Role.USER, false, true));
    AuthenticationUseCase useCase =
        new AuthenticationUseCase(users, raw -> "hash", (e, p) -> {}, user -> "token");

    assertThatThrownBy(() -> useCase.register("Ada", "ada@example.com", "password123"))
        .isInstanceOf(ConflictException.class);
  }

  @Test
  void loginVerifiesCredentialsAndReturnsToken() {
    FakeUsers users =
        new FakeUsers(new AppUser("1", "Ada", "ada@example.com", "hash", Role.USER, false, true));
    java.util.List<String> verified = new java.util.ArrayList<>();
    AuthenticationUseCase useCase =
        new AuthenticationUseCase(
            users,
            raw -> "hash",
            (email, password) -> verified.add(email + ":" + password),
            user -> "token");

    String token = useCase.login("ada@example.com", "password123");

    assertThat(token).isEqualTo("token");
    assertThat(verified).containsExactly("ada@example.com:password123");
  }

  private static final class FakeUsers implements UserRepositoryPort {
    private AppUser current;
    private AppUser saved;

    private FakeUsers(AppUser current) {
      this.current = current;
    }

    @Override
    public AppUser save(AppUser user) {
      saved = user;
      current = user;
      return user;
    }

    @Override
    public Optional<AppUser> findById(String id) {
      return Optional.ofNullable(current).filter(user -> user.id().equals(id));
    }

    @Override
    public Optional<AppUser> findByEmail(String email) {
      return Optional.ofNullable(current).filter(user -> user.email().equals(email));
    }

    @Override
    public List<AppUser> findAll() {
      return current == null ? List.of() : List.of(current);
    }
  }
}
