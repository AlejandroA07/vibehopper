package com.vibehopper.application.user.usecase;
import com.vibehopper.application.common.ConflictException; import com.vibehopper.application.common.NotFoundException; import com.vibehopper.application.user.port.*; import com.vibehopper.domain.user.AppUser; import com.vibehopper.domain.user.Role;
public final class AuthenticationUseCase {
    private final UserRepositoryPort users; private final PasswordHasherPort passwordHasher; private final CredentialVerifierPort credentialVerifier; private final TokenIssuerPort tokenIssuer;
    public AuthenticationUseCase(UserRepositoryPort users, PasswordHasherPort passwordHasher, CredentialVerifierPort credentialVerifier, TokenIssuerPort tokenIssuer){this.users=users;this.passwordHasher=passwordHasher;this.credentialVerifier=credentialVerifier;this.tokenIssuer=tokenIssuer;}
    public String register(String name,String email,String rawPassword){ users.findByEmail(email).ifPresent(u->{throw new ConflictException("Email already in use");}); AppUser user=new AppUser(null,name,email,passwordHasher.hash(rawPassword),Role.USER,false,true); return tokenIssuer.issueToken(users.save(user)); }
    public String login(String email,String rawPassword){ credentialVerifier.verify(email,rawPassword); AppUser user=users.findByEmail(email).orElseThrow(()->new NotFoundException("User not found")); return tokenIssuer.issueToken(user); }
}
