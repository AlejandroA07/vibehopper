package com.almeida.socialsyncapi.controller;


import com.almeida.socialsyncapi.domain.enums.Role;
import com.almeida.socialsyncapi.infrastructure.dto.AppUserDTO;
import com.almeida.socialsyncapi.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final AppUserService appUserService;

    @Autowired
    public UserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @PostMapping("/insert")
    public ResponseEntity<AppUserDTO> createUser(@RequestBody AppUserDTO appUserDTO) {
        AppUserDTO newUser = appUserService.createUser(appUserDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppUserDTO> getUserById(@PathVariable String id) {
        Optional<AppUserDTO> userDTO = appUserService.getUserById(id);
        return userDTO.map(ResponseEntity::ok)
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/users")
    public ResponseEntity<List<AppUserDTO>> getAllUsers() {
        List<AppUserDTO> users = appUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<AppUserDTO> updateUser(
            @PathVariable String id,
            @RequestBody AppUserDTO appUserDTO) {

        AppUserDTO updatedUser = appUserService.updateUser(id, appUserDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/del/user/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String id) {
        appUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/changeRole/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppUserDTO> changeUserRole(@PathVariable String id, @RequestParam Role role) {
        AppUserDTO updatedUser = appUserService.changeUserRole(id, role);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }
}
