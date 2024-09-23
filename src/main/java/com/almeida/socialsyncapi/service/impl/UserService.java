package com.almeida.socialsyncapi.service.impl;

import com.almeida.socialsyncapi.domain.User;
import com.almeida.socialsyncapi.dto.UserDTO;
import com.almeida.socialsyncapi.infrastructure.mapper.UserMapper;
import com.almeida.socialsyncapi.repository.UserRepository;
import com.almeida.socialsyncapi.service.UserServiceInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserServiceInt {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    public Optional<UserDTO> getUserById(String id) {

        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO updateUser(String id, UserDTO userDTO) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));


        User updatedUser = new User.UserBuilder()
                .setId(existingUser.getId())
                .setName(userDTO.name() != null ? userDTO.name() : existingUser.getName())
                .setEmail(userDTO.email() != null ? userDTO.email() : existingUser.getEmail())
                .setPassword(existingUser.getPassword())
                .build();


        userRepository.save(updatedUser);


        return userMapper.toDTO(updatedUser);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }
}
