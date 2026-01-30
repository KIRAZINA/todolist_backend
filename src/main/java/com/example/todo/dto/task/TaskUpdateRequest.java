package com.example.todo.dto.task;

import com.example.todo.entity.Task;
import com.example.todo.validation.ValidEnumValue;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO for updating an existing task.
 * All fields are optional.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUpdateRequest {

    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @ValidEnumValue(enumClass = Task.Priority.class, message = "Invalid priority value")
    private String priority;

    @ValidEnumValue(enumClass = Task.Status.class, message = "Invalid status value")
    @Builder.Default
    private String status = "TODO";

    private LocalDate dueDate;
}