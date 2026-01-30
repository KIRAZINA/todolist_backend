package com.example.todo.exception;

import com.example.todo.dto.common.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.authentication.BadCredentialsException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler with structured error responses and logging.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    // 403
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<?>> handleForbidden(ForbiddenException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    // 400 — @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        log.debug("Validation failed: {}", fieldErrors);

        ApiResponse<?> response = ApiResponse.error("Validation failed", HttpStatus.BAD_REQUEST)
                .toBuilder()  // ← Теперь работает!
                .data(fieldErrors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        }
        log.debug("Constraint violation: {}", errors);

        ApiResponse<?> response = ApiResponse.error("Invalid input", HttpStatus.BAD_REQUEST)
                .toBuilder()  // ← Теперь работает!
                .data(errors)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 401
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        return buildErrorResponse("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }

    // 400 — бизнес‑логика
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper methods
    private ResponseEntity<ApiResponse<?>> buildErrorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message, status)
                        .toBuilder()
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    private ResponseEntity<ApiResponse<?>> buildErrorResponse(String message, HttpStatus status, Map<String, String> details) {
        return ResponseEntity.status(status)
                .body(ApiResponse.error(message, status)
                        .toBuilder()
                        .data(details)
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}