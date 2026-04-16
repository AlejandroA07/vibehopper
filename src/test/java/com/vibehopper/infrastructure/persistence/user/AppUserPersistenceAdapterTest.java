package com.vibehopper.infrastructure.persistence.user;

import com.vibehopper.domain.user.AppUser;
import com.vibehopper.domain.user.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserPersistenceAdapterTest {
    @Mock private SpringDataAppUserRepository repository;

    @Test
    void saveMapsDomainToDocumentAndBack() {
        AppUserPersistenceAdapter adapter = new AppUserPersistenceAdapter(repository);
        when(repository.save(any(AppUserDocument.class))).thenAnswer(invocation -> {
            AppUserDocument document = invocation.getArgument(0);
            document.setId("user-1");
            return document;
        });

        AppUser saved = adapter.save(new AppUser(null, "Ada", "ada@example.com", "hash", Role.USER, false, true));

        assertThat(saved.id()).isEqualTo("user-1");
        assertThat(saved.email()).isEqualTo("ada@example.com");
        verify(repository).save(any(AppUserDocument.class));
    }

    @Test
    void findByEmailMapsDocumentToDomain() {
        AppUserPersistenceAdapter adapter = new AppUserPersistenceAdapter(repository);
        AppUserDocument document = new AppUserDocument();
        document.setId("user-1");
        document.setName("Ada");
        document.setEmail("ada@example.com");
        document.setPassword("hash");
        document.setRole(Role.ADMIN);
        document.setEnabled(true);

        when(repository.findByEmail("ada@example.com")).thenReturn(Optional.of(document));

        assertThat(adapter.findByEmail("ada@example.com")).get().extracting(AppUser::role).isEqualTo(Role.ADMIN);
    }

    @Test
    void findAllMapsDocumentsToDomainUsers() {
        AppUserPersistenceAdapter adapter = new AppUserPersistenceAdapter(repository);
        AppUserDocument document = new AppUserDocument();
        document.setId("user-1");
        document.setName("Ada");
        document.setEmail("ada@example.com");
        document.setPassword("hash");
        document.setRole(Role.USER);
        document.setEnabled(true);
        when(repository.findAll()).thenReturn(List.of(document));

        assertThat(adapter.findAll()).extracting(AppUser::email).containsExactly("ada@example.com");
    }
}
