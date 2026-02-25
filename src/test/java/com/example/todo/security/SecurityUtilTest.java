package com.example.todo.security;

import com.example.todo.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .role("USER")
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldGetCurrentUsernameWhenAuthenticated() {
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<String> username = SecurityUtil.getCurrentUsername();

        assertTrue(username.isPresent());
        assertEquals("testuser", username.get());
    }

    @Test
    void shouldReturnEmptyWhenNotAuthenticated() {
        Optional<String> username = SecurityUtil.getCurrentUsername();

        assertTrue(username.isEmpty());
    }

    @Test
    void shouldGetCurrentUserWhenAuthenticated() {
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<User> user = SecurityUtil.getCurrentUser();

        assertTrue(user.isPresent());
        assertEquals("testuser", user.get().getUsername());
        assertEquals("test@example.com", user.get().getEmail());
        assertEquals(1L, user.get().getId());
    }

    @Test
    void shouldReturnEmptyUserWhenNotAuthenticated() {
        Optional<User> user = SecurityUtil.getCurrentUser();

        assertTrue(user.isEmpty());
    }

    @Test
    void shouldHandleNonCustomUserDetailsPrincipal() {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "stringPrincipal", null, java.util.List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Optional<String> username = SecurityUtil.getCurrentUsername();
        Optional<User> user = SecurityUtil.getCurrentUser();

        assertTrue(username.isEmpty());
        assertTrue(user.isEmpty());
    }
}
