package com.example.todo.util;

import com.example.todo.dto.user.UserLoginRequest;
import com.example.todo.dto.user.UserRegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Base class for integration tests.
 * Provides common setup, utilities, and helper methods.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Register a user and return the JWT token.
     */
    protected String registerAndLogin(String username, String password, String email) throws Exception {
        // Register
        UserRegisterRequest registerRequest = UserRegisterRequest.builder()
                .username(username)
                .password(password)
                .email(email)
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        // Login
        UserLoginRequest loginRequest = UserLoginRequest.builder()
                .username(username)
                .password(password)
                .build();

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        return extractToken(loginResult);
    }

    /**
     * Extract JWT token from login response.
     */
    protected String extractToken(MvcResult result) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.path("data").path("token").asText();
    }

    /**
     * Create Bearer token header value.
     */
    protected String bearer(String token) {
        return "Bearer " + token;
    }

    /**
     * Register a default test user and return token.
     */
    protected String registerDefaultUser() throws Exception {
        return registerAndLogin("testuser", "password123", "test@example.com");
    }

    /**
     * Register an admin user and return token.
     * Note: This requires manual role assignment in the database or service.
     */
    protected String registerAdminUser() throws Exception {
        return registerAndLogin("admin", "adminpass123", "admin@example.com");
    }

    /**
     * Register multiple users and return their tokens.
     */
    protected String[] registerMultipleUsers(int count) throws Exception {
        String[] tokens = new String[count];
        for (int i = 0; i < count; i++) {
            tokens[i] = registerAndLogin(
                    "user" + (i + 1),
                    "password123",
                    "user" + (i + 1) + "@example.com"
            );
        }
        return tokens;
    }
}
