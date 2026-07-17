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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
  private final JwtAuthenticationFilter jwtFilter;
  private final UserDetailsService userDetailsService;
  private final CorsProperties cors;
  private final RestAuthenticationEntryPoint authenticationEntryPoint;
  private final RestAccessDeniedHandler accessDeniedHandler;

  public SecurityConfiguration(
      JwtAuthenticationFilter jwtFilter,
      UserDetailsService userDetailsService,
      CorsProperties cors,
      RestAuthenticationEntryPoint authenticationEntryPoint,
      RestAccessDeniedHandler accessDeniedHandler) {
    this.jwtFilter = jwtFilter;
    this.userDetailsService = userDetailsService;
    this.cors = cors;
    this.authenticationEntryPoint = authenticationEntryPoint;
    this.accessDeniedHandler = accessDeniedHandler;
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    CookieCsrfTokenRepository csrfRepository = new CookieCsrfTokenRepository();
    csrfRepository.setCookieCustomizer(
        cookie -> cookie.httpOnly(true).sameSite("Strict").path("/"));

    http.csrf(csrf -> csrf.csrfTokenRepository(csrfRepository))
        .cors(c -> c.configurationSource(corsConfigurationSource()))
        .authorizeHttpRequests(
            r ->
                r.requestMatchers("/error")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/csrf")
                    .permitAll()
                    .requestMatchers("/api/auth/register", "/api/auth/login")
                    .permitAll()
                    .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/**")
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
        .exceptionHandling(
            e ->
                e.authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(accessDeniedHandler))
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider p = new DaoAuthenticationProvider(userDetailsService);
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
    cfg.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-XSRF-TOKEN"));
    cfg.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", cfg);
    return src;
  }
}
