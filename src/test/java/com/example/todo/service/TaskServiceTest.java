package com.example.todo.service;

import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.exception.ForbiddenException;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.mapper.TaskMapper;
import com.example.todo.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository taskRepository;
    @Mock TaskMapper taskMapper;
    @Mock UserService userService;
    @Mock AuditService auditService;

    @InjectMocks TaskService taskService;

    @Test
    void shouldThrowForbiddenWhenNotOwner() {
        User owner = User.builder().id(1L).build();
        User intruder = User.builder().id(2L).build();
        Task task = Task.builder().id(1L).user(owner).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(ForbiddenException.class, () ->
                taskService.getTaskById(1L, intruder));
    }

    @Test
    void shouldThrowNotFoundWhenTaskMissing() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.getTaskById(999L, User.builder().id(1L).build()));
    }
}