package com.example.todo.dto.user;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO returned after successful registration/login or when fetching user info.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}