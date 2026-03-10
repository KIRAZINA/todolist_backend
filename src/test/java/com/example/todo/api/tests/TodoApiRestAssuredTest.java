package com.example.todo.api.tests;

import com.example.todo.util.RestAssuredTestBase;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;

/**
 * Comprehensive API tests demonstrating RestAssured best practices.
 * Tests various aspects of the Todo API including authentication,
 * authorization, validation, pagination, and error handling.
 */
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class TodoApiRestAssuredTest extends RestAssuredTestBase {

    @Test
    @DisplayName("Should validate complete user authentication flow")
    void shouldValidateCompleteAuthenticationFlow() {
        long timestamp = System.currentTimeMillis();
        // Register user
        Map<String, Object> registerRequest = new HashMap<>();
        registerRequest.put("username", "flow_test_user_" + timestamp);
        registerRequest.put("password", "SecureFlow123!");
        registerRequest.put("email", "flow_" + timestamp + "@example.com");

        given()
                .contentType(ContentType.JSON)
                .body(registerRequest)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("username", equalTo("flow_test_user_" + timestamp))
                .body("email", equalTo("flow_" + timestamp + "@example.com"))
                .body("$", hasKey("id"))
                .body("$", hasKey("createdAt"));

        // Login
        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("username", "flow_test_user_" + timestamp);
        loginRequest.put("password", "SecureFlow123!");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when()
                .post("/api/auth/login")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("token", notNullValue())
                .body("type", equalTo("Bearer"))
                .extract()
                .response();

        // Verify JWT token structure
        String token = response.jsonPath().getString("token");
        String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length);

        // Use token to access protected endpoint
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/tasks")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("Should handle task CRUD operations with full validation")
    void shouldHandleTaskCrudOperations() {
        long timestamp = System.currentTimeMillis();
        String token = registerAndLogin("crud_user_" + timestamp, "CrudPass123!", "crud_" + timestamp + "@example.com");

        // Create task
        Response createResponse = given()
                .header("Authorization", bearer(token))
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "title": "Complete API Testing",
                        "description": "Write comprehensive API tests",
                        "priority": "HIGH",
                        "status": "IN_PROGRESS",
                        "dueDate": "2026-12-31"
                    }
                    """)
                .when()
                .post("/api/tasks")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("title", equalTo("Complete API Testing"))
                .body("priority", equalTo("HIGH"))
                .body("status", equalTo("IN_PROGRESS"))
                .extract()
                .response();

        Integer taskId = createResponse.jsonPath().getInt("id");

        // Get task
        given()
                .header("Authorization", bearer(token))
                .pathParam("id", taskId)
                .when()
                .get("/api/tasks/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title", equalTo("Complete API Testing"))
                .body("id", equalTo(taskId));

        // Update task
        given()
                .header("Authorization", bearer(token))
                .pathParam("id", taskId)
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "title": "Complete API Testing - Updated",
                        "status": "DONE"
                    }
                    """)
                .when()
                .put("/api/tasks/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("title", equalTo("Complete API Testing - Updated"))
                .body("status", equalTo("DONE"));

        // Delete task
        given()
                .header("Authorization", bearer(token))
                .pathParam("id", taskId)
                .when()
                .delete("/api/tasks/{id}")
                .then()
                .statusCode(200);

        // Verify deletion
        given()
                .header("Authorization", bearer(token))
                .pathParam("id", taskId)
                .when()
                .get("/api/tasks/{id}")
                .then()
                .statusCode(404);
    }

    @ParameterizedTest
    @DisplayName("Should create tasks with different priorities")
    @ValueSource(strings = {"LOW", "MEDIUM", "HIGH"})
    void shouldCreateTasksWithDifferentPriorities(String priority) {
        long timestamp = System.currentTimeMillis();
        String token = registerAndLogin("priority_user_" + timestamp, "PriorityPass123!", "priority_" + timestamp + "@example.com");

        given()
                .header("Authorization", bearer(token))
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {
                        "title": "Task with %s Priority",
                        "priority": "%s",
                        "status": "TODO"
                    }
                    """, priority, priority))
                .when()
                .post("/api/tasks")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("priority", equalTo(priority))
                .body("title", containsString(priority));
    }

    @Test
    @DisplayName("Should validate API response schemas")
    void shouldValidateApiResponseSchemas() {
        long timestamp = System.currentTimeMillis();
        String token = registerAndLogin("schema_user_" + timestamp, "SchemaPass123!", "schema_" + timestamp + "@example.com");

        // Validate user response schema
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {
                        "username": "schema_validation_user_%d",
                        "password": "SchemaPass123!",
                        "email": "schema_%d@example.com"
                    }
                    """, timestamp, timestamp))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/user-response-schema.json"));

        // Validate task response schema
        given()
                .header("Authorization", bearer(token))
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "title": "Schema Validation Task",
                        "description": "Task for schema validation",
                        "priority": "HIGH",
                        "status": "TODO"
                    }
                    """)
                .when()
                .post("/api/tasks")
                .then()
                .statusCode(201)
                .body(matchesJsonSchemaInClasspath("schemas/task-response-schema.json"));
    }

    @Test
    @DisplayName("Should handle pagination and sorting")
    void shouldHandlePaginationAndSorting() {
        long timestamp = System.currentTimeMillis();
        String token = registerAndLogin("pagination_user_" + timestamp, "PagePass123!", "page_" + timestamp + "@example.com");

        // Create multiple tasks
        for (int i = 1; i <= 15; i++) {
            given()
                    .header("Authorization", bearer(token))
                    .contentType(ContentType.JSON)
                    .body(String.format("""
                        {
                            "title": "Task %02d",
                            "priority": "MEDIUM",
                            "status": "TODO"
                        }
                        """, i))
                    .when()
                    .post("/api/tasks")
                    .then()
                    .statusCode(201);
        }

        // Test pagination
        given()
                .header("Authorization", bearer(token))
                .queryParam("page", 0)
                .queryParam("size", 5)
                .when()
                .get("/api/tasks")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(5));

        // Test sorting
        given()
                .header("Authorization", bearer(token))
                .queryParam("page", 0)
                .queryParam("size", 3)
                .queryParam("sortBy", "title")
                .queryParam("sortDir", "desc")
                .when()
                .get("/api/tasks")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(3))
                .body("[0].title", equalTo("Task 15"))
                .body("[1].title", equalTo("Task 14"))
                .body("[2].title", equalTo("Task 13"));
    }

    @Test
    @DisplayName("Should handle concurrent requests")
    void shouldHandleConcurrentRequests() throws InterruptedException {
        long timestamp = System.currentTimeMillis();
        String token = registerAndLogin("concurrent_user_" + timestamp, "ConcurrentPass123!", "concurrent_" + timestamp + "@example.com");

        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Create multiple tasks concurrently
        for (int i = 1; i <= 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                given()
                        .header("Authorization", bearer(token))
                        .contentType(ContentType.JSON)
                        .body(String.format("""
                            {
                                "title": "Concurrent Task %d",
                                "priority": "MEDIUM",
                                "status": "TODO"
                            }
                            """, taskId))
                        .when()
                        .post("/api/tasks")
                        .then()
                        .statusCode(201);
            });
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(10, TimeUnit.SECONDS));

        // Verify all tasks were created
        given()
                .header("Authorization", bearer(token))
                .when()
                .get("/api/tasks")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasSize(5));
    }

    @Test
    @DisplayName("Should handle error responses")
    void shouldHandleErrorResponses() {
        long timestamp = System.currentTimeMillis();
        
        // Test validation errors
        given()
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "username": "",
                        "password": "",
                        "email": "invalid-email"
                    }
                    """)
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(400)
                .contentType(ContentType.JSON);

        // Test not found errors
        String token = registerAndLogin("error_user_" + timestamp, "ErrorPass123!", "error_" + timestamp + "@example.com");
        
        given()
                .header("Authorization", bearer(token))
                .pathParam("id", 99999)
                .when()
                .get("/api/tasks/{id}")
                .then()
                .statusCode(404)
                .contentType(ContentType.JSON)
                .body("error", notNullValue())
                .body("error", equalTo("Task not found"));

        // Test unauthorized access
        given()
                .pathParam("id", 1)
                .when()
                .get("/api/tasks/{id}")
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Should validate response times")
    void shouldValidateResponseTimes() {
        long timestamp = System.currentTimeMillis();
        String token = registerAndLogin("performance_user_" + timestamp, "PerfPass123!", "perf_" + timestamp + "@example.com");

        // Authentication endpoints should be fast
        given()
                .contentType(ContentType.JSON)
                .body(String.format("""
                    {
                        "username": "perf_test_user_%d",
                        "password": "PerfTest123!",
                        "email": "perf_%d@example.com"
                    }
                    """, timestamp, timestamp))
                .when()
                .post("/api/auth/register")
                .then()
                .statusCode(200)
                .time(lessThan(2000L));

        // Task operations should be responsive
        given()
                .header("Authorization", bearer(token))
                .contentType(ContentType.JSON)
                .body("""
                    {
                        "title": "Performance Test Task",
                        "priority": "LOW",
                        "status": "TODO"
                    }
                    """)
                .when()
                .post("/api/tasks")
                .then()
                .statusCode(201)
                .time(lessThan(1000L));

        // Retrieval should be fast
        given()
                .header("Authorization", bearer(token))
                .when()
                .get("/api/tasks")
                .then()
                .statusCode(200)
                .time(lessThan(500L));
    }
}
