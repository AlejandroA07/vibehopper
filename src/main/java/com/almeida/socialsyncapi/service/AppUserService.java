package com.almeida.socialsyncapi.service;

import com.almeida.socialsyncapi.domain.AppUser;
import com.almeida.socialsyncapi.domain.enums.Role;
import com.almeida.socialsyncapi.infrastructure.EntityMapper.UserMapper;
import com.almeida.socialsyncapi.infrastructure.dto.AppUserDTO;
import com.almeida.socialsyncapi.infrastructure.exception.DuplicateResourceException;
import com.almeida.socialsyncapi.infrastructure.exception.ResourceNotFoundException;
import com.almeida.socialsyncapi.repository.AppUserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {
  private final AppUserRepository appUserRepository;
  private final UserMapper userMapper;

  @Autowired
  public AppUserService(AppUserRepository appUserRepository, UserMapper userMapper) {
    this.appUserRepository = appUserRepository;
    this.userMapper = userMapper;
  }

  // CREATE
  public AppUserDTO createUser(AppUserDTO appUserDTO) {

    if (appUserRepository.findByEmail(appUserDTO.email()).isPresent()) {
      throw new DuplicateResourceException("Email already in use");
    }

    AppUser appUser = userMapper.toEntity(appUserDTO);
    AppUser savedAppUser = appUserRepository.save(appUser);
    return userMapper.toDTO(savedAppUser);
  }

  // GET BY ID
  public Optional<AppUserDTO> getUserById(String id) {

    return appUserRepository.findById(id).map(userMapper::toDTO);
  }

  public AppUserDTO getCurrentUser(AppUser user) {
    return userMapper.toDTO(user);
  }

  // GET ALL
  public List<AppUserDTO> getAllUsers() {
    List<AppUser> appUsers = appUserRepository.findAll();
    return appUsers.stream().map(userMapper::toDTO).collect(Collectors.toList());
  }

  // UPDATE
  public AppUserDTO updateUser(String id, AppUserDTO appUserDTO) {

    AppUser existingAppUser =
        appUserRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    AppUser updatedAppUser = UserMapper.updateUserFromDTO(existingAppUser, appUserDTO);

    appUserRepository.save(updatedAppUser);

    return userMapper.toDTO(updatedAppUser);
  }

  // DELETE
  public void deleteById(String id) {
    if (!appUserRepository.existsById(id)) {
      throw new ResourceNotFoundException("User not found");
    }
    appUserRepository.deleteById(id);
  }

  // CHANGE ROLE
  public AppUserDTO changeUserRole(String userId, Role newRole) {

    AppUser user =
        appUserRepository
            .findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    AppUser updatedUser =
        AppUser.builder()
            .setId(user.getId())
            .setName(user.getName())
            .setEmail(user.getEmail())
            .setPassword(user.getPassword())
            .setRole(newRole)
            .setLocked(user.getLocked())
            .setEnabled(user.getEnabled())
            .build();

    appUserRepository.save(updatedUser);

    return userMapper.toDTO(updatedUser);
  }
}
