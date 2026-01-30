package com.example.todo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Utility class for JWT token generation and validation.
 * Updated for jjwt 0.12+.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Generate JWT token from authentication.
     */
    public String generateToken(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Get username from token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsParser()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Validate JWT token.
     */
    public boolean validateToken(String token) {
        try {
            getClaimsParser().parseSignedClaims(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("JWT token is malformed: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Create parser with signing key.
     */
    private JwtParser getClaimsParser() {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build();
    }
}