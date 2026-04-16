package com.vibehopper.domain.user;
public record AppUser(String id, String name, String email, String password, Role role, boolean locked, boolean enabled) {
    public AppUser withRole(Role newRole) { return new AppUser(id, name, email, password, newRole, locked, enabled); }
}
