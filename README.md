# To-Do List Backend — Spring Boot 3 + JWT + OpenAPI

![Java 17](https://img.shields.io/badge/Java-17-orange)
![Spring Boot 3
![MIT License](https://img.shields.io/badge/License-MIT-blue.svg)
![Coverage](https://img.shields.io/badge/Test_Coverage-95%25%2B-brightgreen)

A **production-ready** REST API for a To-Do List application built with modern Spring Boot 3, JWT authentication, clean architecture, and excellent test coverage.

## Features
- Java 17 + Spring Boot 3.3+
- **Security**:
  - JWT-based authentication & authorization
  - Role-based access (USER / ADMIN)
  - Secure password handling (BCrypt)
  - CORS configuration for frontend integration
  - Rate limiting support
  - Security event logging
- **Performance**:
  - Database indexing for high-performance queries
  - Caching support (UserDetailsService usage)
  - Optimized JPA queries
- **Documentation**:
  - OpenAPI 3.1 specification with beautiful Swagger UI
- **Quality**:
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

## Environment Variables
For production deployment, set the following environment variables:

| Variable | Description | Default (Dev Only) |
|----------|-------------|-------------------|
| `JWT_SECRET` | Secret key for JWT token signing (min 256 bits) | `default-dev-secret-key-at-least-256-bits-1234567890abcdef` |
| `JWT_EXPIRATION_MS` | JWT token expiration time in milliseconds | `86400000` (24 hours) |

**⚠️ IMPORTANT:** Never use the default JWT secret in production! Generate a secure random key:
```bash
# Linux/Mac
openssl rand -base64 64

# Or use online generator
# https://www.grc.com/passwords.htm
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

## Testing
The project includes a comprehensive test suite covering all layers:

### Running Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=JwtAuthenticationFilterTest
```

### Test Coverage
- **Unit Tests**: Security layers, Services, Utilities
- **Integration Tests**: Controllers (MockMvc), Database (H2)
- **Security Tests**: Auth flows, JWT validation, Filter chains

The test suite uses a dedicated `IntegrationTestBase` and `TestDataBuilder` for consistent and reliable testing.

## Database (dev profile)
- H2 in-memory database
- Console available at: http://localhost:8080/h2-console  
  (JDBC URL: `jdbc:h2:mem:todo`)
