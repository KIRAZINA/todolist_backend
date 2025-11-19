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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private String user1Token;
    private String user2Token;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login user1
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new UserRegisterRequest("user1", "pass123", "user1@example.com"))));

        MvcResult login1 = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserLoginRequest("user1", "pass123"))))
                .andReturn();

        user1Token = extractToken(login1);

        // Register and login user2
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        new UserRegisterRequest("user2", "pass123", "user2@example.com"))));

        MvcResult login2 = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UserLoginRequest("user2", "pass123"))))
                .andReturn();

        user2Token = extractToken(login2);
    }

    private String extractToken(MvcResult result) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("token").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }

    @Test
    void shouldCreateAndGetOwnTask() throws Exception {
        TaskCreateRequest create = new TaskCreateRequest();
        create.setTitle("Learn Spring Boot");
        create.setDescription("Backend project");
        create.setPriority("HIGH");                    // String
        create.setStatus("TODO");                      // String
        create.setDueDate(LocalDate.of(2025, 12, 31)); // ← LocalDate!

        MvcResult result = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("Learn Spring Boot"))
                .andReturn();

        Long taskId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        mockMvc.perform(get("/api/tasks/" + taskId)
                        .header("Authorization", bearer(user1Token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Learn Spring Boot"));
    }

    @Test
    void shouldForbidAccessToForeignTask() throws Exception {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("Secret Task");
        req.setPriority("LOW");
        req.setStatus("TODO");
        req.setDueDate(null); // можно null

        MvcResult createRes = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andReturn();

        Long taskId = objectMapper.readTree(createRes.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        mockMvc.perform(get("/api/tasks/" + taskId)
                        .header("Authorization", bearer(user2Token)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void shouldUpdateAndDeleteOwnTask() throws Exception {
        TaskCreateRequest create = new TaskCreateRequest();
        create.setTitle("To Update");
        create.setPriority("MEDIUM");
        create.setStatus("IN_PROGRESS");

        MvcResult res = mockMvc.perform(post("/api/tasks")
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(create)))
                .andReturn();

        Long id = objectMapper.readTree(res.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        TaskUpdateRequest update = new TaskUpdateRequest();
        update.setTitle("Updated Successfully");
        update.setStatus("DONE");

        mockMvc.perform(put("/api/tasks/" + id)
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("DONE"));

        mockMvc.perform(delete("/api/tasks/" + id)
                        .header("Authorization", bearer(user1Token)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/tasks/" + id)
                        .header("Authorization", bearer(user1Token)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAllCompletedTasks() throws Exception {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("Done Task");
        req.setStatus("DONE");

        mockMvc.perform(post("/api/tasks")
                        .header("Authorization", bearer(user1Token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/tasks/completed")
                        .header("Authorization", bearer(user1Token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("All completed tasks deleted"));
    }
}