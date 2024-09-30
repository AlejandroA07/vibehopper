package com.almeida.socialsyncapi.infrastructure.EntityMapper;

import com.almeida.socialsyncapi.domain.User;
import com.almeida.socialsyncapi.infrastructure.dto.UserDTO;
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

    public static User updateUserFromDTO(User existingUser, UserDTO userDTO) {
        return new User.UserBuilder()
                .setId(existingUser.getId())
                .setName(userDTO.name() != null ? userDTO.name() : existingUser.getName())
                .setEmail(userDTO.email() != null ? userDTO.email() : existingUser.getEmail())
                .setPassword(existingUser.getPassword())
                .build();
    }
}
