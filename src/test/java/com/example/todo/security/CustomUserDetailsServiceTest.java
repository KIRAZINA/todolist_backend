package com.example.todo.security;

import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomUserDetailsService.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .enabled(true)
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertEquals(1, userDetails.getAuthorities().size());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void shouldLoadUserWithMultipleRoles() {
        // Arrange
        User adminUser = User.builder()
                .id(2L)
                .username("admin")
                .password("encodedPassword")
                .email("admin@example.com")
                .roles(Set.of("USER", "ADMIN"))
                .enabled(true)
                .build();

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("admin");

        // Assert
        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals(2, userDetails.getAuthorities().size());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent")
        );

        assertEquals("User not found: nonexistent", exception.getMessage());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void shouldHandleNullUsername() {
        // Arrange
        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername(null)
        );
    }

    @Test
    void shouldHandleEmptyUsername() {
        // Arrange
        when(userRepository.findByUsername("")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("")
        );
    }

    @Test
    void shouldReturnCustomUserDetailsInstance() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .password("encodedPassword")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Assert
        assertTrue(userDetails instanceof CustomUserDetails);
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        assertEquals(1L, customUserDetails.getId());
        assertEquals("test@example.com", customUserDetails.getEmail());
    }
}
