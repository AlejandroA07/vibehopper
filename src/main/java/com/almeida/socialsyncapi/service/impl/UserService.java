package com.almeida.socialsyncapi.service.impl;

import com.almeida.socialsyncapi.domain.User;
import com.almeida.socialsyncapi.dto.UserDTO;
import com.almeida.socialsyncapi.infrastructure.mapper.UserMapper;
import com.almeida.socialsyncapi.repository.UserRepository;
import com.almeida.socialsyncapi.service.UserServiceInt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserServiceInt {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public Optional<UserDTO> getUserById(String id) {

        return userRepository.findById(id)
                .map(userMapper::toDTO);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }
}
