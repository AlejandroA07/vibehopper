package com.almeida.socialsyncapi.service;

import com.almeida.socialsyncapi.domain.User;

import java.util.Optional;

public interface UserServiceInt {

    public User createUser(User user);

    public Optional<User> getUserById(String id);
}
