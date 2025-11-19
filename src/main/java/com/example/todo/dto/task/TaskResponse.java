package com.example.todo.dto.task;

import com.example.todo.entity.Task;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO returned in task lists and details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Task.Priority priority;
    private Task.Status status;
    private LocalDate dueDate;
    private Long userId;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}