package com.almeida.socialsyncapi.infrastructure.dto.auth;

public class AuthenticationRequest {

    private String email;
    String password;

    public AuthenticationRequest() {
    }

    public AuthenticationRequest(String email, String password) {
        this.email = email;    //TODO Basic auth implementation
        this.password = password;
    }

    // Getters y Setters
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
        private String email;
        private String password;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public AuthenticationRequest build() {
            AuthenticationRequest request = new AuthenticationRequest();
            request.setEmail(this.email);
            request.setPassword(this.password);
            return request;
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
