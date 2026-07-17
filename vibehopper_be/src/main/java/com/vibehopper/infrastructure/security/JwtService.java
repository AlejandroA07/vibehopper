package com.vibehopper.infrastructure.security;

import com.vibehopper.application.user.port.TokenIssuerPort;
import com.vibehopper.domain.user.AppUser;
import com.vibehopper.infrastructure.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService implements TokenIssuerPort {
  private final JwtProperties props;

  public JwtService(JwtProperties props) {
    this.props = props;
  }

  public String issueToken(AppUser user) {
    return generateToken(new HashMap<>(), user.email());
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    return resolver.apply(extractAllClaims(token));
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    return userDetails.getUsername().equals(extractUsername(token)) && !isTokenExpired(token);
  }

  private String generateToken(Map<String, Object> claims, String subject) {
    Instant now = Instant.now();
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plus(props.expiration())))
        .signWith(signingKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date());
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(signingKey()).build().parseClaimsJws(token).getBody();
  }

  private Key signingKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(props.secret()));
  }
}
