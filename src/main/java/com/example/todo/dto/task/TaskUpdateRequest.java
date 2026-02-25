package com.example.todo.dto.task;

import com.example.todo.entity.Task;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskUpdateRequest {

    private String title;

    private String description;

    private Task.Priority priority;

    private Task.Status status;

    private LocalDate dueDate;
}
