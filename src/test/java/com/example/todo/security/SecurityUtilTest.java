package com.example.todo.security;

import com.example.todo.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SecurityUtil.
 */
class SecurityUtilTest {

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldGetCurrentUsernameWhenAuthenticated() {
        // Arrange
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Optional<String> username = SecurityUtil.getCurrentUsername();

        // Assert
        assertTrue(username.isPresent());
        assertEquals("testuser", username.get());
    }

    @Test
    void shouldReturnEmptyWhenNotAuthenticated() {
        // Arrange - no authentication set

        // Act
        Optional<String> username = SecurityUtil.getCurrentUsername();

        // Assert
        assertTrue(username.isEmpty());
    }

    @Test
    void shouldReturnEmptyWhenAuthenticationIsNull() {
        // Arrange
        SecurityContextHolder.getContext().setAuthentication(null);

        // Act
        Optional<String> username = SecurityUtil.getCurrentUsername();

        // Assert
        assertTrue(username.isEmpty());
    }

    @Test
    void shouldGetCurrentUserWhenAuthenticated() {
        // Arrange
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Optional<User> user = SecurityUtil.getCurrentUser();

        // Assert
        assertTrue(user.isPresent());
        assertEquals("testuser", user.get().getUsername());
        assertEquals("test@example.com", user.get().getEmail());
        assertEquals(1L, user.get().getId());
        assertTrue(user.get().getRoles().contains("USER"));
    }

    @Test
    void shouldGetCurrentUserWithMultipleRoles() {
        // Arrange
        User adminUser = User.builder()
                .id(2L)
                .username("admin")
                .email("admin@example.com")
                .roles(Set.of("USER", "ADMIN"))
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(adminUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Optional<User> user = SecurityUtil.getCurrentUser();

        // Assert
        assertTrue(user.isPresent());
        assertEquals(2, user.get().getRoles().size());
        assertTrue(user.get().getRoles().contains("USER"));
        assertTrue(user.get().getRoles().contains("ADMIN"));
    }

    @Test
    void shouldReturnEmptyUserWhenNotAuthenticated() {
        // Arrange - no authentication

        // Act
        Optional<User> user = SecurityUtil.getCurrentUser();

        // Assert
        assertTrue(user.isEmpty());
    }

    @Test
    void shouldRequireCurrentUsernameWhenAuthenticated() {
        // Arrange
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        String username = SecurityUtil.requireCurrentUsername();

        // Assert
        assertEquals("testuser", username);
    }

    @Test
    void shouldThrowExceptionWhenRequireCurrentUsernameNotAuthenticated() {
        // Arrange - no authentication

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                SecurityUtil::requireCurrentUsername
        );
        assertEquals("User is not authenticated", exception.getMessage());
    }

    @Test
    void shouldRequireCurrentUserWhenAuthenticated() {
        // Arrange
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        User user = SecurityUtil.requireCurrentUser();

        // Assert
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenRequireCurrentUserNotAuthenticated() {
        // Arrange - no authentication

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                SecurityUtil::requireCurrentUser
        );
        assertEquals("User is not authenticated", exception.getMessage());
    }

    @Test
    void shouldHandleNonCustomUserDetailsPrincipal() {
        // Arrange - use a different principal type
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "stringPrincipal", null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        Optional<String> username = SecurityUtil.getCurrentUsername();
        Optional<User> user = SecurityUtil.getCurrentUser();

        // Assert
        assertTrue(username.isEmpty());
        assertTrue(user.isEmpty());
    }
}
