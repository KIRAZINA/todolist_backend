package com.example.todo.exception;

/**
 * Thrown when user lacks permission.
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}