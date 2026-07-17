package com.vibehopper.infrastructure.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.lang.NonNull;
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

  public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
    this.jwtService = jwtService;
    this.userDetailsService = userDetailsService;
  }

  protected void doFilterInternal(
      @NonNull HttpServletRequest req, @NonNull HttpServletResponse res, @NonNull FilterChain chain)
      throws ServletException, IOException {
    String header = req.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      chain.doFilter(req, res);
      return;
    }
    String jwt = header.substring(7);
    String email = jwtService.extractUsername(jwt);
    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails user = userDetailsService.loadUserByUsername(email);
      if (jwtService.isTokenValid(jwt, user)) {
        UsernamePasswordAuthenticationToken token =
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
        SecurityContextHolder.getContext().setAuthentication(token);
      }
    }
    chain.doFilter(req, res);
  }
}
