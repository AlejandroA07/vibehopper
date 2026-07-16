package com.almeida.socialsyncapi.domain;

import com.almeida.socialsyncapi.domain.enums.Role;
import java.util.Collection;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Document(collection = "users")
public class AppUser implements UserDetails {

  @Id private String id;
  private String name;
  private String email;
  private String password;
  private Role role;
  private Boolean locked = false;
  private Boolean enabled = true;

  public AppUser() {}

  private AppUser(UserBuilder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.email = builder.email;
    this.password = builder.password;
    this.role = builder.role;
    this.locked = builder.locked;
    this.enabled = builder.enabled;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public Role getRole() {
    return role;
  }

  public Boolean getLocked() {
    return locked;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    if (role == null) {
      return List.of();
    }
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !Boolean.TRUE.equals(locked);
  }

  @Override
  public boolean isEnabled() {
    return !Boolean.FALSE.equals(enabled);
  }

  public static UserBuilder builder() {
    return new UserBuilder();
  }

  public static class UserBuilder {
    private String id;
    private String name;
    private String email;
    private String password;
    public Role role = Role.USER;
    private Boolean locked = false;
    private Boolean enabled = true;

    public UserBuilder setId(String id) {
      this.id = id;
      return this;
    }

    public UserBuilder setName(String name) {
      this.name = name;
      return this;
    }

    public UserBuilder setEmail(String email) {
      this.email = email;
      return this;
    }

    public UserBuilder setPassword(String password) {
      this.password = password;
      return this;
    }

    public UserBuilder setRole(Role role) {
      this.role = role;
      return this;
    }

    public UserBuilder setLocked(Boolean locked) {
      this.locked = locked;
      return this;
    }

    public UserBuilder setEnabled(Boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    public AppUser build() {
      return new AppUser(this);
    }
  }
}
