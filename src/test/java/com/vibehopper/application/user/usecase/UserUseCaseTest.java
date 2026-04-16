package com.vibehopper.application.user.usecase;

import com.vibehopper.application.common.NotFoundException;
import com.vibehopper.application.user.port.UserRepositoryPort;
import com.vibehopper.domain.user.AppUser;
import com.vibehopper.domain.user.Role;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserUseCaseTest {
    @Test
    void changeRoleKeepsUserDataAndUpdatesRole() {
        FakeUsers users = new FakeUsers(new AppUser("user-1", "Ada", "ada@example.com", "hash", Role.USER, false, true));
        UserUseCase useCase = new UserUseCase(users);

        AppUser updated = useCase.changeRole("user-1", Role.ADMIN);

        assertThat(updated.role()).isEqualTo(Role.ADMIN);
        assertThat(updated.email()).isEqualTo("ada@example.com");
        assertThat(users.saved.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void missingUserReturnsNotFound() {
        UserUseCase useCase = new UserUseCase(new FakeUsers(null));

        assertThatThrownBy(() -> useCase.findById("missing")).isInstanceOf(NotFoundException.class);
    }

    private static final class FakeUsers implements UserRepositoryPort {
        private AppUser current;
        private AppUser saved;

        private FakeUsers(AppUser current) {
            this.current = current;
        }

        @Override public AppUser save(AppUser user) { saved = user; current = user; return user; }
        @Override public Optional<AppUser> findById(String id) { return Optional.ofNullable(current).filter(user -> user.id().equals(id)); }
        @Override public Optional<AppUser> findByEmail(String email) { return Optional.ofNullable(current).filter(user -> user.email().equals(email)); }
        @Override public List<AppUser> findAll() { return current == null ? List.of() : List.of(current); }
    }
}
