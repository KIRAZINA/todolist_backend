package com.example.todo.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtTokenProviderTest {

    @Autowired JwtTokenProvider jwtTokenProvider;

    @Test
    void shouldGenerateAndValidateToken() {
        Authentication auth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                "testuser", null, List.of(new SimpleGrantedAuthority("USER")));

        String token = jwtTokenProvider.generateToken(auth);

        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("testuser", jwtTokenProvider.getUsernameFromToken(token));
    }
}