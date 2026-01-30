package com.example.todo.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Test
    void shouldGenerateAndValidateToken() {
        CustomUserDetails userDetails = new CustomUserDetails(
                com.example.todo.entity.User.builder()
                        .username("testuser")
                        .password("password")
                        .email("test@example.com")
                        .roles(Set.of("USER"))
                        .build()
        );
        
        Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateToken(auth);

        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    void shouldExtractUsernameFromToken() {
        CustomUserDetails userDetails = new CustomUserDetails(
                com.example.todo.entity.User.builder()
                        .username("john.doe")
                        .password("password")
                        .email("john@example.com")
                        .roles(Set.of("USER"))
                        .build()
        );
        
        Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateToken(auth);
        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals("john.doe", username);
    }

    @Test
    void shouldRejectExpiredToken() {
        // Create an expired token manually
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        String expiredToken = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000)) // Expired 5 seconds ago
                .signWith(key)
                .compact();

        assertFalse(jwtTokenProvider.validateToken(expiredToken));
    }

    @Test
    void shouldRejectMalformedToken() {
        String malformedToken = "this.is.not.a.valid.jwt";

        assertFalse(jwtTokenProvider.validateToken(malformedToken));
    }

    @Test
    void shouldRejectTokenWithInvalidSignature() {
        // Create token with different secret
        SecretKey wrongKey = Keys.hmacShaKeyFor("different-secret-key-at-least-256-bits-1234567890abcdef".getBytes());
        String tokenWithWrongSignature = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(wrongKey)
                .compact();

        assertFalse(jwtTokenProvider.validateToken(tokenWithWrongSignature));
    }

    @Test
    void shouldRejectEmptyToken() {
        assertFalse(jwtTokenProvider.validateToken(""));
    }

    @Test
    void shouldRejectNullToken() {
        assertFalse(jwtTokenProvider.validateToken(null));
    }

    @Test
    void shouldGenerateTokenWithCorrectClaims() {
        CustomUserDetails userDetails = new CustomUserDetails(
                com.example.todo.entity.User.builder()
                        .username("testuser")
                        .password("password")
                        .email("test@example.com")
                        .roles(Set.of("USER"))
                        .build()
        );
        
        Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateToken(auth);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void shouldHandleMultipleRoles() {
        CustomUserDetails userDetails = new CustomUserDetails(
                com.example.todo.entity.User.builder()
                        .username("admin")
                        .password("password")
                        .email("admin@example.com")
                        .roles(Set.of("USER", "ADMIN"))
                        .build()
        );
        
        Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateToken(auth);

        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("admin", jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    void shouldGenerateDifferentTokensForDifferentUsers() {
        CustomUserDetails userDetails1 = new CustomUserDetails(
                com.example.todo.entity.User.builder()
                        .username("user1")
                        .password("password")
                        .email("user1@example.com")
                        .roles(Set.of("USER"))
                        .build()
        );
        
        CustomUserDetails userDetails2 = new CustomUserDetails(
                com.example.todo.entity.User.builder()
                        .username("user2")
                        .password("password")
                        .email("user2@example.com")
                        .roles(Set.of("USER"))
                        .build()
        );
        
        Authentication auth1 = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails1, null, userDetails1.getAuthorities());
        Authentication auth2 = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                userDetails2, null, userDetails2.getAuthorities());

        String token1 = jwtTokenProvider.generateToken(auth1);
        String token2 = jwtTokenProvider.generateToken(auth2);

        assertNotEquals(token1, token2);
        assertEquals("user1", jwtTokenProvider.getUsernameFromToken(token1));
        assertEquals("user2", jwtTokenProvider.getUsernameFromToken(token2));
    }
}
