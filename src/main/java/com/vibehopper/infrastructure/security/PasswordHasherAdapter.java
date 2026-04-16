package com.vibehopper.infrastructure.security;
import com.vibehopper.application.user.port.PasswordHasherPort; import org.springframework.security.crypto.password.PasswordEncoder; import org.springframework.stereotype.Component;
@Component public class PasswordHasherAdapter implements PasswordHasherPort { private final PasswordEncoder encoder; public PasswordHasherAdapter(PasswordEncoder encoder){this.encoder=encoder;} public String hash(String rawPassword){return encoder.encode(rawPassword);} }
