package com.example.todo.service;

import com.example.todo.dto.common.PageResponse;
import com.example.todo.dto.task.TaskCreateRequest;
import com.example.todo.dto.task.TaskResponse;
import com.example.todo.dto.task.TaskUpdateRequest;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.exception.ForbiddenException;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.mapper.TaskMapper;
import com.example.todo.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing To-Do tasks.
 * Enforces ownership: users can only access their own tasks unless ADMIN.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final AuditService auditService;

    /**
     * Create a new task for the given user.
     */
    @Transactional
    public TaskResponse createTask(TaskCreateRequest request, User user) {
        Task task = taskMapper.toEntity(request);
        task.setUser(user);
        task.setCreatedBy(auditService.getCurrentUsername());
        task.setUpdatedBy(auditService.getCurrentUsername());

        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    /**
     * Get task by ID with ownership check.
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        validateTaskOwnership(task, currentUser);
        return taskMapper.toResponse(task);
    }

    /**
     * List tasks with pagination, filtering, sorting.
     */
    @Transactional(readOnly = true)
    public PageResponse<TaskResponse> getTasks(
            User currentUser,
            Integer page,
            Integer size,
            String sortBy,
            String sortDir,
            Task.Status status) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Task> taskPage;
        if (status != null) {
            taskPage = taskRepository.findByUserAndStatus(currentUser, status, pageable);
        } else {
            taskPage = taskRepository.findByUser(currentUser, pageable);
        }

        List<TaskResponse> content = taskPage.getContent().stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<TaskResponse>builder()
                .content(content)
                .pageNumber(taskPage.getNumber())
                .pageSize(taskPage.getSize())
                .totalElements(taskPage.getTotalElements())
                .totalPages(taskPage.getTotalPages())
                .last(taskPage.isLast())
                .build();
    }

    /**
     * Update task with ownership check.
     */
    @Transactional
    public TaskResponse updateTask(Long id, TaskUpdateRequest request, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        validateTaskOwnership(task, currentUser);

        taskMapper.updateEntityFromRequest(request, task);
        task.setUpdatedBy(auditService.getCurrentUsername());

        task = taskRepository.save(task);
        return taskMapper.toResponse(task);
    }

    /**
     * Delete task with ownership check.
     */
    @Transactional
    public void deleteTask(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        validateTaskOwnership(task, currentUser);
        taskRepository.delete(task);
    }

    /**
     * Bulk delete all completed tasks for the user.
     */
    @Transactional
    public void deleteAllCompleted(User currentUser) {
        taskRepository.deleteAllCompletedByUser(currentUser);
    }

    /**
     * Validate that the task belongs to the current user or user is ADMIN.
     */
    private void validateTaskOwnership(Task task, User currentUser) {
        boolean isOwner = task.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().contains("ADMIN") || 
                         currentUser.getRoles().contains("ROLE_ADMIN");
        
        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You do not have permission to access this task");
        }
    }

    /**
     * Get tasks due on a specific date (for reminder scheduler).
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksDueOnDate(java.time.LocalDate date) {
        return taskRepository.findByDueDate(date);
    }
}