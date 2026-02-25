package com.example.todo.service;

import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.exception.ResourceNotFoundException;
import com.example.todo.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock TaskRepository taskRepository;

    @InjectMocks TaskService taskService;

    @Test
    void shouldThrowNotFoundWhenNotOwner() {
        User owner = User.builder().id(1L).build();
        User intruder = User.builder().id(2L).role("USER").build();
        Task task = Task.builder().id(1L).user(owner).build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.getTaskById(1L, intruder));
        
        verify(taskRepository).findById(1L);
    }

    @Test
    void shouldThrowNotFoundWhenTaskMissing() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                taskService.getTaskById(999L, User.builder().id(1L).build()));
    }
}
