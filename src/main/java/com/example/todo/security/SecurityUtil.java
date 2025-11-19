package com.example.todo.security;

import com.example.todo.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Helper to get current authenticated user.
 */
public class SecurityUtil {

    public static Optional<String> getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return Optional.of(details.getUsername());
        }
        return Optional.empty();
    }

    public static Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof CustomUserDetails details) {
            return Optional.of(User.builder()
                    .id(details.getId())
                    .username(details.getUsername())
                    .email(details.getEmail())
                    .roles(details.getAuthorities().stream()
                            .map(a -> a.getAuthority().replace("ROLE_", ""))
                            .collect(java.util.stream.Collectors.toSet()))
                    .build());
        }
        return Optional.empty();
    }
}