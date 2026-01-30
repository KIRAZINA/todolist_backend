package com.example.todo.security;

import com.example.todo.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Helper to get current authenticated user.
 */
import java.util.stream.Collectors;

/**
 * Utility class for accessing security context information.
 * 
 * <p>All methods return Optional to handle cases where user is not authenticated.
 * Use the helper methods that throw exceptions if you need to ensure authentication.
 */
public class SecurityUtil {

    /**
     * Get the current authenticated username.
     * 
     * @return Optional containing username if authenticated, empty otherwise
     */
    public static Optional<String> getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return Optional.of(details.getUsername());
        }
        return Optional.empty();
    }

    /**
     * Get the current authenticated user.
     * 
     * @return Optional containing User if authenticated, empty otherwise
     */
    public static Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return Optional.of(User.builder()
                    .id(details.getId())
                    .username(details.getUsername())
                    .email(details.getEmail())
                    .roles(details.getAuthorities().stream()
                            .map(a -> a.getAuthority().replace("ROLE_", ""))
                            .collect(Collectors.toSet()))
                    .build());
        }
        return Optional.empty();
    }

    /**
     * Get the current authenticated username, throwing exception if not authenticated.
     * 
     * @return username
     * @throws IllegalStateException if user is not authenticated
     */
    public static String requireCurrentUsername() {
        return getCurrentUsername()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }

    /**
     * Get the current authenticated user, throwing exception if not authenticated.
     * 
     * @return User object
     * @throws IllegalStateException if user is not authenticated
     */
    public static User requireCurrentUser() {
        return getCurrentUser()
                .orElseThrow(() -> new IllegalStateException("User is not authenticated"));
    }
}