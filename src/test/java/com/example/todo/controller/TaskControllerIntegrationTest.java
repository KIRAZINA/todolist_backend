package com.example.todo.controller;

import com.example.todo.dto.task.TaskCreateRequest;
import com.example.todo.dto.task.TaskUpdateRequest;
import com.example.todo.dto.user.UserLoginRequest;
import com.example.todo.dto.user.UserRegisterRequest;
import com.example.todo.entity.Task;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TaskControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String user1Token;
    private String user2Token;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new UserRegisterRequest("user1", "password123", "user1@example.com"))))
                .andExpect(status().isOk());

        MvcResult login1 = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserLoginRequest("user1", "password123"))))
                .andExpect(status().isOk())
                .andReturn();

        user1Token = extractToken(login1);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new UserRegisterRequest("user2", "password123", "user2@example.com"))))
                .andExpect(status().isOk());

        MvcResult login2 = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserLoginRequest("user2", "password123"))))
                .andExpect(status().isOk())
                .andReturn();

        user2Token = extractToken(login2);
    }

    private String extractToken(MvcResult result) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("token").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    void shouldCreateAndGetOwnTask() throws Exception {
        TaskCreateRequest create = new TaskCreateRequest();
        create.setTitle("Learn Spring Boot");
        create.setDescription("Backend project");
        create.setPriority(Task.Priority.HIGH);
        create.setStatus(Task.Status.TODO);
        create.setDueDate(LocalDate.of(2026, 12, 31));

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Learn Spring Boot"))
                .andReturn();

        Long taskId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("id").asLong();

        mockMvc.perform(get("/api/tasks/" + taskId)
                        .header("Authorization", bearer(user1Token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Learn Spring Boot"));
    }

    @Test
    void shouldReturnNotFoundForForeignTask() throws Exception {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("Secret Task");
        req.setPriority(Task.Priority.LOW);
        req.setStatus(Task.Status.TODO);
        req.setDueDate(null);

        MvcResult createRes = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn();

        Long taskId = objectMapper.readTree(createRes.getResponse().getContentAsString())
                .path("id").asLong();

        mockMvc.perform(get("/api/tasks/" + taskId)
                        .header("Authorization", bearer(user2Token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateAndDeleteOwnTask() throws Exception {
        TaskCreateRequest create = new TaskCreateRequest();
        create.setTitle("To Update");
        create.setPriority(Task.Priority.MEDIUM);
        create.setStatus(Task.Status.IN_PROGRESS);

        MvcResult res = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andReturn();

        Long id = objectMapper.readTree(res.getResponse().getContentAsString())
                .path("id").asLong();

        TaskUpdateRequest update = new TaskUpdateRequest();
        update.setTitle("Updated Successfully");
        update.setStatus(Task.Status.DONE);

        mockMvc.perform(put("/api/tasks/" + id)
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));

        mockMvc.perform(delete("/api/tasks/" + id)
                        .header("Authorization", bearer(user1Token)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks/" + id)
                        .header("Authorization", bearer(user1Token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetAllTasksWithPagination() throws Exception {
        // Create multiple tasks
        for (int i = 1; i <= 5; i++) {
            TaskCreateRequest create = new TaskCreateRequest();
            create.setTitle("Task " + i);
            create.setPriority(Task.Priority.LOW);
            create.setStatus(Task.Status.TODO);
            
            mockMvc.perform(post("/api/tasks")
                            .header("Authorization", bearer(user1Token))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(create)))
                    .andExpect(status().isCreated());
        }

        // Get first page
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", bearer(user1Token))
                        .param("page", "0")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));

        // Get second page
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", bearer(user1Token))
                        .param("page", "1")
                        .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldNotAllowDeleteForeignTask() throws Exception {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("User1 Private");
        req.setPriority(Task.Priority.HIGH);
        req.setStatus(Task.Status.TODO);

        MvcResult res = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn();

        Long taskId = objectMapper.readTree(res.getResponse().getContentAsString())
                .path("id").asLong();

        // User2 tries to delete user1's task
        mockMvc.perform(delete("/api/tasks/" + taskId)
                        .header("Authorization", bearer(user2Token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRejectUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnEmptyListForNewUser() throws Exception {
        mockMvc.perform(get("/api/tasks")
                        .header("Authorization", bearer(user1Token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldUpdateOnlyOwnFields() throws Exception {
        // Create task
        TaskCreateRequest create = new TaskCreateRequest();
        create.setTitle("Original Title");
        create.setDescription("Original Desc");
        create.setPriority(Task.Priority.LOW);
        create.setStatus(Task.Status.TODO);

        MvcResult res = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andReturn();

        Long id = objectMapper.readTree(res.getResponse().getContentAsString())
                .path("id").asLong();

        // Update only title
        TaskUpdateRequest update = new TaskUpdateRequest();
        update.setTitle("New Title");

        mockMvc.perform(put("/api/tasks/" + id)
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.description").value("Original Desc"));
    }
}
