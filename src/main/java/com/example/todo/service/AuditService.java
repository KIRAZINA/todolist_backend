package com.example.todo.service;

import com.example.todo.security.SecurityUtil;
import org.springframework.stereotype.Service;

/**
 * Utility service to handle audit fields (createdBy, updatedBy).
 * Uses current authenticated username from SecurityContext.
 */
@Service
public class AuditService {

    /**
     * Get current authenticated username.
     * If no user is authenticated, returns "system".
     */
    public String getCurrentUsername() {
        return SecurityUtil.getCurrentUsername().orElse("system");
    }
}