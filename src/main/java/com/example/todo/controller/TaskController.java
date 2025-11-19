package com.example.todo.controller;

import com.example.todo.dto.common.PageResponse;
import com.example.todo.dto.task.TaskCreateRequest;
import com.example.todo.dto.task.TaskResponse;
import com.example.todo.dto.task.TaskUpdateRequest;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.exception.ForbiddenException;
import com.example.todo.security.SecurityUtil;
import com.example.todo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for task management.
 * All operations require authenticated user (enforced via JWT filter).
 */
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "CRUD operations for To-Do tasks")
public class TaskController {

    private final TaskService taskService;

    private User getCurrentUser() {
        return SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new ForbiddenException("User not authenticated"));
    }

    @Operation(summary = "Create a new task", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Task created",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Unauthorized")
    })
    @PostMapping
    public com.example.todo.dto.common.ApiResponse<TaskResponse> createTask(
            @Valid @RequestBody TaskCreateRequest request) {
        TaskResponse response = taskService.createTask(request, getCurrentUser());
        return com.example.todo.dto.common.ApiResponse.created(response, "Task created successfully");
    }

    @Operation(summary = "Get task by ID", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task found",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    public com.example.todo.dto.common.ApiResponse<TaskResponse> getTask(@PathVariable Long id) {
        TaskResponse response = taskService.getTaskById(id, getCurrentUser());
        return com.example.todo.dto.common.ApiResponse.success(response, "Task retrieved successfully");
    }

    @Operation(summary = "List tasks with pagination", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tasks retrieved",
                    content = @Content(schema = @Schema(implementation = PageResponse.class))
            )
    })
    @GetMapping
    public com.example.todo.dto.common.ApiResponse<PageResponse<TaskResponse>> getTasks(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) Task.Status status) {

        PageResponse<TaskResponse> response = taskService.getTasks(
                getCurrentUser(), page, size, sortBy, sortDir, status);
        return com.example.todo.dto.common.ApiResponse.success(response, "Tasks retrieved successfully");
    }

    @Operation(summary = "Update task", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task updated",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PutMapping("/{id}")
    public com.example.todo.dto.common.ApiResponse<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequest request) {
        TaskResponse response = taskService.updateTask(id, request, getCurrentUser());
        return com.example.todo.dto.common.ApiResponse.success(response, "Task updated successfully");
    }

    @Operation(summary = "Delete task", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task deleted"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @DeleteMapping("/{id}")
    public com.example.todo.dto.common.ApiResponse<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id, getCurrentUser());
        return com.example.todo.dto.common.ApiResponse.success(null, "Task deleted successfully");
    }

    @Operation(summary = "Delete all completed tasks", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Completed tasks deleted")
    })
    @DeleteMapping("/completed")
    public com.example.todo.dto.common.ApiResponse<Void> deleteCompleted() {
        taskService.deleteAllCompleted(getCurrentUser());
        return com.example.todo.dto.common.ApiResponse.success(null, "All completed tasks deleted");
    }

    @Operation(summary = "Get tasks due today", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Due today tasks count",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @GetMapping("/due-today")
    public com.example.todo.dto.common.ApiResponse<Map<String, Object>> getDueToday() {
        var tasks = taskService.getTasksDueOnDate(LocalDate.now());
        Map<String, Object> data = new HashMap<>();
        data.put("count", tasks.size());
        data.put("tasks", tasks.stream().map(Task::getTitle).toList());
        return com.example.todo.dto.common.ApiResponse.success(data, "Due today tasks count");
    }
}