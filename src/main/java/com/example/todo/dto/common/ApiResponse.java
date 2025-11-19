package com.example.todo.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper used throughout the application.
 *
 * @param <T> type of the data payload
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Schema(description = "Standard API response envelope")
public class ApiResponse<T> {

    @Schema(description = "Indicates whether the request was successful", example = "true")
    private boolean success;

    @Schema(description = "Human-readable message", example = "Task created successfully")
    private String message;

    @Schema(description = "Response payload (can be null on error)")
    private T data;

    @Schema(description = "Timestamp of the response", example = "2025-11-19T18:30:45.123")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "200")
    private int statusCode;

    // Factory methods

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .statusCode(200)
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .statusCode(201)
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .statusCode(statusCode)
                .build();
    }

    // Convenience overload for existing code that passes HttpStatus
    public static <T> ApiResponse<T> error(String message, HttpStatus status) {
        return error(message, status.value());
    }
}