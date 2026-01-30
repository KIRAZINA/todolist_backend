package com.example.todo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Main application class for the To-Do List backend.
 * This class bootstraps the Spring Boot application.
 *
 * @SpringBootApplication enables auto-configuration, component scanning, and other Spring Boot features.
 */
@SpringBootApplication
@EnableCaching
public class TodoApplication {

    /**
     * Entry point method to run the application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }
}