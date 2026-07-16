package com.almeida.socialsyncapi.infrastructure.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

  @NotBlank @Size(max = 120) private String name;

  @NotBlank @Email @Size(max = 254) private String email;

  @NotBlank @Size(min = 8, max = 128) private String password;

  public RegisterRequest() {}

  public RegisterRequest(String name, String email, String password) {
    this.name = name;
    this.email = email;
    this.password = password;
  }

  // Getters y Setters
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  // Builder pattern implementation
  public static class Builder {
    private String name;
    private String email;
    private String password;

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder email(String email) {
      this.email = email;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public RegisterRequest build() {
      RegisterRequest request = new RegisterRequest();
      request.setName(this.name);
      request.setEmail(this.email);
      request.setPassword(this.password);
      return request;
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
