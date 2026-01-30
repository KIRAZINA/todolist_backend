package com.example.todo.util;

import com.example.todo.dto.task.TaskCreateRequest;
import com.example.todo.dto.task.TaskUpdateRequest;
import com.example.todo.dto.user.UserLoginRequest;
import com.example.todo.dto.user.UserRegisterRequest;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;

import java.time.LocalDate;
import java.util.Set;

/**
 * Utility class for building test data objects.
 * Provides consistent test data across all test classes.
 */
public class TestDataBuilder {

    // ==================== User Builders ====================

    public static User.UserBuilder defaultUser() {
        return User.builder()
                .id(1L)
                .username("testuser")
                .password("$2a$10$encodedPassword")
                .email("test@example.com")
                .roles(Set.of("USER"))
                .enabled(true);
    }

    public static User.UserBuilder adminUser() {
        return User.builder()
                .id(2L)
                .username("admin")
                .password("$2a$10$encodedPassword")
                .email("admin@example.com")
                .roles(Set.of("USER", "ADMIN"))
                .enabled(true);
    }

    public static User.UserBuilder userWithId(Long id) {
        return defaultUser().id(id);
    }

    public static User.UserBuilder userWithUsername(String username) {
        return defaultUser().username(username).email(username + "@example.com");
    }

    // ==================== Task Builders ====================

    public static Task.TaskBuilder defaultTask() {
        return Task.builder()
                .id(1L)
                .title("Test Task")
                .description("Test Description")
                .priority(Task.Priority.MEDIUM)
                .status(Task.Status.TODO)
                .dueDate(LocalDate.now().plusDays(7))
                .user(defaultUser().build());
    }

    public static Task.TaskBuilder taskWithStatus(Task.Status status) {
        return defaultTask().status(status);
    }

    public static Task.TaskBuilder taskWithPriority(Task.Priority priority) {
        return defaultTask().priority(priority);
    }

    public static Task.TaskBuilder taskWithDueDate(LocalDate dueDate) {
        return defaultTask().dueDate(dueDate);
    }

    public static Task.TaskBuilder taskForUser(User user) {
        return defaultTask().user(user);
    }

    // ==================== DTO Builders ====================

    public static UserRegisterRequest.UserRegisterRequestBuilder defaultRegisterRequest() {
        return UserRegisterRequest.builder()
                .username("newuser")
                .password("password123")
                .email("newuser@example.com");
    }

    public static UserLoginRequest.UserLoginRequestBuilder defaultLoginRequest() {
        return UserLoginRequest.builder()
                .username("testuser")
                .password("password123");
    }

    public static TaskCreateRequest.TaskCreateRequestBuilder defaultTaskCreateRequest() {
        return TaskCreateRequest.builder()
                .title("New Task")
                .description("Task Description")
                .priority("MEDIUM")
                .status("TODO")
                .dueDate(LocalDate.now().plusDays(7));
    }

    public static TaskUpdateRequest.TaskUpdateRequestBuilder defaultTaskUpdateRequest() {
        return TaskUpdateRequest.builder()
                .title("Updated Task")
                .description("Updated Description")
                .priority("HIGH")
                .status("IN_PROGRESS")
                .dueDate(LocalDate.now().plusDays(14));
    }

    // ==================== Specific Test Scenarios ====================

    public static UserRegisterRequest invalidEmailRequest() {
        return defaultRegisterRequest()
                .email("invalid-email")
                .build();
    }

    public static UserRegisterRequest shortPasswordRequest() {
        return defaultRegisterRequest()
                .password("short")
                .build();
    }

    public static UserRegisterRequest duplicateUsernameRequest(String existingUsername) {
        return defaultRegisterRequest()
                .username(existingUsername)
                .build();
    }

    public static TaskCreateRequest invalidPriorityRequest() {
        return defaultTaskCreateRequest()
                .priority("INVALID")
                .build();
    }

    public static TaskCreateRequest pastDueDateRequest() {
        return defaultTaskCreateRequest()
                .dueDate(LocalDate.now().minusDays(1))
                .build();
    }

    public static TaskCreateRequest minimalTaskRequest() {
        return TaskCreateRequest.builder()
                .title("Minimal Task")
                .build();
    }

    // ==================== Collections ====================

    public static User[] multipleUsers(int count) {
        User[] users = new User[count];
        for (int i = 0; i < count; i++) {
            users[i] = userWithId((long) (i + 1))
                    .username("user" + (i + 1))
                    .email("user" + (i + 1) + "@example.com")
                    .build();
        }
        return users;
    }

    public static Task[] multipleTasks(User user, int count) {
        Task[] tasks = new Task[count];
        for (int i = 0; i < count; i++) {
            tasks[i] = defaultTask()
                    .id((long) (i + 1))
                    .title("Task " + (i + 1))
                    .user(user)
                    .build();
        }
        return tasks;
    }

    public static Task[] tasksWithDifferentStatuses(User user) {
        return new Task[]{
                taskWithStatus(Task.Status.TODO).id(1L).user(user).build(),
                taskWithStatus(Task.Status.IN_PROGRESS).id(2L).user(user).build(),
                taskWithStatus(Task.Status.DONE).id(3L).user(user).build()
        };
    }
}
