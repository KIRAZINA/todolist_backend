package com.example.todo.security;

import com.example.todo.entity.User;
import com.example.todo.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for JwtAuthenticationFilter.
 */
@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private CustomUserDetails testUserDetails;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        User testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded")
                .roles(Set.of("USER"))
                .build();
        testUserDetails = new CustomUserDetails(testUser);
    }

    @Test
    void shouldAuthenticateWithValidToken() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(testUserDetails);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("testuser", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {
        // Arrange
        String token = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void shouldRejectExpiredToken() throws Exception {
        // Arrange
        String token = "expired.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(false);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueWhenNoAuthorizationHeader() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider, never()).validateToken(anyString());
    }

    @Test
    void shouldContinueWhenAuthorizationHeaderDoesNotStartWithBearer() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Basic sometoken");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtTokenProvider, never()).validateToken(anyString());
    }

    @Test
    void shouldHandleMalformedAuthorizationHeader() throws Exception {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer");

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldHandleUserNotFoundInDatabase() throws Exception {
        // Arrange
        String token = "valid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.getUsernameFromToken(token)).thenReturn("nonexistent");
        when(userDetailsService.loadUserByUsername("nonexistent")).thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));

        // Act
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldHandleExceptionGracefully() throws Exception {
        // Arrange
        String token = "problematic.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtTokenProvider.validateToken(token)).thenThrow(new RuntimeException("Token processing error"));

        // Act & Assert - should not throw exception
        assertDoesNotThrow(() -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));
        verify(filterChain).doFilter(request, response);
    }
}
