package com.vibehopper.infrastructure.persistence.user;

import com.vibehopper.application.common.ConflictException;
import com.vibehopper.domain.user.AppUser;
import com.vibehopper.domain.user.Role;
import com.vibehopper.support.PostgresContainerSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AppUserPersistenceAdapterIntegrationTest extends PostgresContainerSupport {
    @Autowired
    private AppUserPersistenceAdapter adapter;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE event_attendees, events, app_users CASCADE");
    }

    @Test
    void saveAndFindByEmailUseRealCaseInsensitivePostgresLookup() {
        AppUser saved = adapter.save(new AppUser(null, "Ada", "ada@example.com", "hash", Role.USER, false, true));

        assertThat(saved.id()).isNotBlank();
        assertThat(adapter.findByEmail("ADA@EXAMPLE.COM"))
                .isPresent()
                .get()
                .extracting(AppUser::email, AppUser::name, AppUser::role)
                .containsExactly("ada@example.com", "Ada", Role.USER);
    }

    @Test
    void duplicateEmailConflictComesFromDatabaseUniqueIndex() {
        adapter.save(new AppUser(null, "Ada", "ada@example.com", "hash", Role.USER, false, true));

        assertThatThrownBy(() -> adapter.save(new AppUser(null, "Ada 2", "ADA@example.com", "hash", Role.ADMIN, false, true)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Email already in use");
    }
}
