package com.vibehopper.infrastructure.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
  private final RestAuthenticationEntryPoint authenticationEntryPoint;

  public JwtAuthenticationFilter(
      JwtService jwtService,
      UserDetailsService userDetailsService,
      RestAuthenticationEntryPoint authenticationEntryPoint) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
    this.authenticationEntryPoint = authenticationEntryPoint;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain chain)
      throws ServletException, IOException {
    String header = req.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      chain.doFilter(req, res);
      return;
    }
    try {
      String jwt = header.substring(7);
      String email = jwtService.extractUsername(jwt);
      if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails user = userDetailsService.loadUserByUsername(email);
        if (!jwtService.isTokenValid(jwt, user)) {
          authenticationEntryPoint.commence(
              req, res, new BadCredentialsException("Invalid bearer token"));
          return;
        }
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(token);
      }
    } catch (JwtException | UsernameNotFoundException | IllegalArgumentException exception) {
      SecurityContextHolder.clearContext();
      authenticationEntryPoint.commence(
          req, res, new BadCredentialsException("Invalid bearer token", exception));
      return;
    }
    chain.doFilter(req, res);
  }
}
