package com.almeida.socialsyncapi.infrastructure.mapper;

import com.almeida.socialsyncapi.domain.User;
import com.almeida.socialsyncapi.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public class UserMapper {
    public UserDTO toDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }


    public User toEntity(UserDTO userDTO) {
        return new User.UserBuilder()
                .setId(userDTO.id())
                .setName(userDTO.name())
                .setEmail(userDTO.email())
                .build();
    }
}
