package com.vibehopper.application.user.port;

public interface CredentialVerifierPort {
  void verify(String email, String rawPassword);
}
