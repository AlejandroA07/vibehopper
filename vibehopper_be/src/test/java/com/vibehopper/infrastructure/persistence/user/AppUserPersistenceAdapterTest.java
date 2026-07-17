package com.vibehopper.infrastructure.persistence.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vibehopper.domain.user.AppUser;
import com.vibehopper.domain.user.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class AppUserPersistenceAdapterTest {
  @Mock private AppUserJpaRepository repository;

  @Test
  void saveMapsDomainToEntityAndBack() {
    AppUserPersistenceAdapter adapter = new AppUserPersistenceAdapter(repository);
    ArgumentCaptor<AppUserEntity> captor = ArgumentCaptor.forClass(AppUserEntity.class);
    when(repository.saveAndFlush(any(AppUserEntity.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    AppUser saved =
        adapter.save(new AppUser(null, "Ada", "ada@example.com", "hash", Role.USER, false, true));

    verify(repository).saveAndFlush(captor.capture());
    assertThat(captor.getValue().getId()).isNotNull();
    assertThat(saved.id()).isNotBlank();
    assertThat(saved.email()).isEqualTo("ada@example.com");
  }

  @Test
  void findByEmailUsesCaseInsensitiveRepositoryQuery() {
    AppUserPersistenceAdapter adapter = new AppUserPersistenceAdapter(repository);
    AppUserEntity entity = userEntity(Role.ADMIN);
    when(repository.findByEmailIgnoreCase("Ada@Example.com")).thenReturn(Optional.of(entity));

    assertThat(adapter.findByEmail("Ada@Example.com"))
        .get()
        .extracting(AppUser::role)
        .isEqualTo(Role.ADMIN);
  }

  @Test
  void findAllMapsEntitiesToDomainUsers() {
    AppUserPersistenceAdapter adapter = new AppUserPersistenceAdapter(repository);
    when(repository.findAll()).thenReturn(List.of(userEntity(Role.USER)));

    assertThat(adapter.findAll()).extracting(AppUser::email).containsExactly("ada@example.com");
  }

  @Test
  void duplicateEmailConstraintMapsToConflictException() {
    AppUserPersistenceAdapter adapter = new AppUserPersistenceAdapter(repository);
    when(repository.saveAndFlush(any(AppUserEntity.class)))
        .thenThrow(new DataIntegrityViolationException("duplicate"));

    assertThatThrownBy(
            () ->
                adapter.save(
                    new AppUser(null, "Ada", "ada@example.com", "hash", Role.USER, false, true)))
        .isInstanceOf(com.vibehopper.application.common.ConflictException.class)
        .hasMessage("Email already in use");
  }

  private AppUserEntity userEntity(Role role) {
    AppUserEntity entity = new AppUserEntity();
    entity.setId(UUID.randomUUID());
    entity.setName("Ada");
    entity.setEmail("ada@example.com");
    entity.setPassword("hash");
    entity.setRole(role);
    entity.setEnabled(true);
    return entity;
  }
}
