package com.vibehopper.infrastructure.config;
import com.vibehopper.application.event.port.EventRepositoryPort; import com.vibehopper.application.event.usecase.*; import com.vibehopper.application.user.port.*; import com.vibehopper.application.user.usecase.*; import org.springframework.context.annotation.*;
@Configuration
public class ApplicationUseCaseConfig {
    @Bean EventDeduplicationService eventDeduplicationService(EventRepositoryPort repo){ return new EventDeduplicationService(repo); }
    @Bean EventUseCase eventUseCase(EventRepositoryPort repo, EventDeduplicationService dedup){ return new EventUseCase(repo,dedup); }
    @Bean UserUseCase userUseCase(UserRepositoryPort repo){ return new UserUseCase(repo); }
    @Bean AuthenticationUseCase authenticationUseCase(UserRepositoryPort users, PasswordHasherPort hasher, CredentialVerifierPort verifier, TokenIssuerPort issuer){ return new AuthenticationUseCase(users,hasher,verifier,issuer); }
}
