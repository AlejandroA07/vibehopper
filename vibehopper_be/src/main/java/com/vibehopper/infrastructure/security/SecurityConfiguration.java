package com.vibehopper.infrastructure.security;

import com.vibehopper.infrastructure.config.CorsProperties;
import java.util.List;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
  private final JwtAuthenticationFilter jwtFilter;
  private final UserDetailsService userDetailsService;
  private final CorsProperties cors;

  public SecurityConfiguration(
      JwtAuthenticationFilter jwtFilter,
      UserDetailsService userDetailsService,
      CorsProperties cors) {
    this.jwtFilter = jwtFilter;
    this.userDetailsService = userDetailsService;
    this.cors = cors;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .cors(c -> c.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            r ->
                r.requestMatchers("/error")
                    .permitAll()
                    .requestMatchers("/api/auth/register", "/api/auth/login")
                    .permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/users/*")
                    .permitAll()
                    .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")
                    .requestMatchers(HttpMethod.POST, "/api/events")
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/api/events/*")
                    .hasAnyRole("USER", "ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/api/events/*")
                    .hasAnyRole("USER", "ADMIN")
                    .anyRequest()
                    .authenticated())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider p = new DaoAuthenticationProvider();
    p.setUserDetailsService(userDetailsService);
    p.setPasswordEncoder(passwordEncoder());
    return p;
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception {
    return c.getAuthenticationManager();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(
        cors.allowedOrigins() == null || cors.allowedOrigins().isEmpty()
            ? List.of("http://localhost:5173")
            : cors.allowedOrigins());
    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    cfg.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", cfg);
    return src;
  }
}
