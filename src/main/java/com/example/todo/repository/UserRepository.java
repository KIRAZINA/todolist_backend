package com.example.todo.repository;

import com.example.todo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA repository for {@link User} entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username (case-sensitive).
     * Used during login and registration checks.
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a username already exists.
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email already exists.
     */
    boolean existsByEmail(String email);
}