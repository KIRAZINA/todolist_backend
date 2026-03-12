package com.example.todo.dto.task;

import com.example.todo.entity.Task;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100)
    private String title;

    @Size(max = 1000)
    private String description;

    private Task.Priority priority;

    private Task.Status status;

    private LocalDate dueDate;
}
