package com.example.todo.dto.task;

import com.example.todo.entity.Task;
import com.example.todo.validation.ValidEnumValue;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import jakarta.validation.constraints.FutureOrPresent;
import java.time.LocalDate;


/**
 * DTO for creating a new task.
 */
@Schema(description = "Request to create a new task")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskCreateRequest {

    @Schema(description = "Task title", example = "Learn Spring Boot", required = true)
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Schema(description = "Task description", example = "Study JWT and security")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @Schema(description = "Priority: LOW, MEDIUM, HIGH", example = "HIGH")
    @NotNull(message = "Priority is required")
    @ValidEnumValue(enumClass = Task.Priority.class, message = "Invalid priority value")
    private String priority;

    @Schema(description = "Status: TODO, IN_PROGRESS, DONE", example = "TODO")
    @ValidEnumValue(enumClass = Task.Status.class, message = "Invalid status value")
    @Builder.Default
    private String status = "TODO";

    @Schema(description = "Due date (ISO format)", example = "2025-12-31")
    @FutureOrPresent(message = "Due date cannot be in the past")
    private LocalDate dueDate;
}