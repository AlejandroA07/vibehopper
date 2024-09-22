package com.almeida.socialsyncapi.service;

import com.almeida.socialsyncapi.dto.UserDTO;

import java.util.Optional;

public interface UserServiceInt {

    public Optional<UserDTO> getUserById(String id);
}
