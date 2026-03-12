package com.example.todo.controller;

import com.example.todo.dto.task.TaskRequest;
import com.example.todo.dto.task.TaskResponse;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.security.CurrentUserService;
import com.example.todo.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final CurrentUserService currentUserService;

    private User getCurrentUser() {
        return currentUserService.getCurrentUser()
                .orElseThrow(() -> new ResourceNotFoundException("User not authenticated"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@Valid @RequestBody TaskRequest request) {
        return taskService.createTask(request, getCurrentUser());
    }

    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable Long id) {
        return taskService.getTaskById(id, getCurrentUser());
    }

    @GetMapping
    public List<TaskResponse> getTasks() {
        return taskService.getTasks(getCurrentUser());
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest request) {
        return taskService.updateTask(id, request, getCurrentUser());
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id, getCurrentUser());
    }
}
