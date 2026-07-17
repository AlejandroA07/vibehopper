package com.vibehopper.infrastructure.security;

import com.vibehopper.application.user.port.CredentialVerifierPort;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Component;

@Component
public class CredentialVerifierAdapter implements CredentialVerifierPort {
  private final AuthenticationManager authenticationManager;

  public CredentialVerifierAdapter(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  public void verify(String email, String rawPassword) {
    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, rawPassword));
  }
}
