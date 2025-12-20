package es.museum.asi.security;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import es.museum.asi.config.Properties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenProvider {
  private static final String AUTHORITIES_KEY = "auth";

  private static final Logger log = LoggerFactory.getLogger(TokenProvider.class);

  @Autowired
  private Properties properties;

  public boolean validateToken(String authToken) {
    SecretKey key = Keys.hmacShaKeyFor(properties.getJwtSecretKey().getBytes(StandardCharsets.UTF_8));
    try {
      Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken);
      return true;
    } catch (io.jsonwebtoken.JwtException | IllegalArgumentException ex) {
      log.debug("Invalid JWT: {}", ex.getMessage());
      return false;
    }
  }

  public Authentication getAuthentication(String authToken) {
    SecretKey key = Keys.hmacShaKeyFor(properties.getJwtSecretKey().getBytes(StandardCharsets.UTF_8));
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(authToken).getPayload();
    GrantedAuthority authority = new SimpleGrantedAuthority(claims.get(AUTHORITIES_KEY).toString());
    Collection<GrantedAuthority> authorities = Collections.singleton(authority);
    User user = new User(claims.getSubject(), "", authorities);
    return new UsernamePasswordAuthenticationToken(user, authToken, authorities);
  }

  public String createToken(Authentication authentication) {
    String authority = authentication.getAuthorities().iterator().next().toString();

    long now = (new Date()).getTime();
    Date validity = new Date(now + (properties.getJwtValidity() * 1000));

    SecretKey key = Keys.hmacShaKeyFor(properties.getJwtSecretKey().getBytes(StandardCharsets.UTF_8));
    return Jwts.builder().subject(authentication.getName()).claim(AUTHORITIES_KEY, authority).signWith(key)
        .expiration(validity).compact();
  }
}
