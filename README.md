# To-Do List Backend — Spring Boot 3 + JWT + OpenAPI

![Java 17](https://img.shields.io/badge/Java-17-orange)
![Spring Boot 3
![MIT License](https://img.shields.io/badge/License-MIT-blue.svg)
![Coverage](https://img.shields.io/badge/Test_Coverage-95%25%2B-brightgreen)

A **production-ready** REST API for a To-Do List application built with modern Spring Boot 3, JWT authentication, clean architecture, and excellent test coverage.

## Features
- Java 17 + Spring Boot 3.3+
- JWT-based authentication & authorization
- Task ownership protection (users can only access their own tasks)
- Role-based access (USER / ADMIN)
- OpenAPI 3.1 specification with beautiful Swagger UI
- Pagination, sorting, filtering
- Unified response wrapper (`ApiResponse<T>`)
- Input validation + global exception handling
- 95%+ test coverage (integration + unit tests)
- Clean layered architecture

## Live API Documentation
After starting the app:  
http://localhost:8080/swagger-ui.html

![Swagger UI](https://raw.githubusercontent.com/KIRAZINA/todolist_backend/main/screenshot-swagger.png)

## Quick Start
```bash
git clone https://github.com/KIRAZINA/todolist_backend.git
cd todolist_backend
./mvnw spring-boot:run
```

## Main Endpoints
| Method | Endpoint                     | Description                       |
|--------|------------------------------|-----------------------------------|
| POST   | `/api/auth/register`         | Register new user                 |
| POST   | `/api/auth/login`            | Login → returns JWT               |
| POST   | `/api/tasks`                       | Create task                       |
| GET    | `/api/tasks`                 | List tasks (with pagination, sort, filter) |
| GET    | `/api/tasks/{id}`            | Get task by ID                    |
| PUT    | `/api/tasks/{id}`            | Update task                       |
| DELETE | `/api/tasks/{id}`            | Delete task                       |
| DELETE | `/api/tasks/completed`       | Delete all completed tasks        |
| GET    | `/api/tasks/due-today`       | Get today's due tasks count       |

## Tech Stack
- Spring Boot 3.3+
- Spring Security + JWT
- Spring Data JPA (H2 in-memory for dev)
- Lombok
- SpringDoc OpenAPI 3
- JUnit 5 + MockMvc + Mockito
- Maven

## Running Tests
```bash
./mvnw test
```

## Database (dev profile)
- H2 in-memory database
- Console available at: http://localhost:8080/h2-console  
  (JDBC URL: `jdbc:h2:mem:todo`)
