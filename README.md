# To-Do List Backend — Spring Boot 3 + JWT

![Java 17](https://img.shields.io/badge/Java-17-orange)
![Spring Boot 3.5](https://img.shields.io/badge/Spring_Boot-3.5.6-green)
![MIT License](https://img.shields.io/badge/License-MIT-blue.svg)

A simple REST API for a To-Do List application with JWT authentication.

## Features

- Java 17 + Spring Boot 3.5.6
- **Security**:
  - JWT authentication
  - Secure password storage (BCrypt)
  - CORS configuration
- **Simplicity**:
  - Minimal dependencies
  - H2 in-memory database
  - Direct DTOs without wrapper classes

## Quick Start

```bash
git clone https://github.com/YOUR_USERNAME/todolist_backend.git
cd todolist_backend
mvn spring-boot:run
```

Application runs at http://localhost:8080

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login → returns JWT |
| POST | `/api/tasks` | Create task |
| GET | `/api/tasks` | List tasks (pagination) |
| GET | `/api/tasks/{id}` | Get task by ID |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

## Testing

```bash
# Run all tests
mvn test

# Test coverage: 43 tests
```

## Tech Stack

- Spring Boot 3.5.6
- Spring Security + JWT
- Spring Data JPA (H2)
- JUnit 5 + MockMvc
- Maven

## Database

- H2 in-memory database
- Console: http://localhost:8080/h2-console  
  (JDBC URL: `jdbc:h2:mem:todo`)

## Changes After Simplification

Project was refactored to remove over-engineering:

- ❌ Removed Swagger/OpenAPI
- ❌ Removed MapStruct (manual mapping)
- ❌ Removed caching
- ❌ Removed Flyway/migrations
- ❌ Removed audit fields
- ✅ Simplified DTOs without wrapper classes
- ✅ Simplified security (role as String)
- ✅ H2 database

## License

MIT
