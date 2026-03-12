# To-Do List Backend — Spring Boot 3 + JWT

REST API for managing tasks with JWT authentication.

## Features

- Java 17, Spring Boot 3.5.6
- JWT authentication and user roles
- H2 in-memory database for development and tests
- Request validation
- Explicit error handling with correct HTTP status codes
- RestAssured integration tests plus service unit tests
- Simple, maintainable architecture without over-engineering

## Quick Start

```bash
mvn spring-boot:run
```

The application will start at `http://localhost:8080`.

## Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register |
| POST | `/api/auth/login` | Login (JWT) |
| POST | `/api/tasks` | Create task |
| GET | `/api/tasks` | List tasks (sorted by creation date) |
| GET | `/api/tasks/{id}` | Get task |
| PUT | `/api/tasks/{id}` | Update task |
| DELETE | `/api/tasks/{id}` | Delete task |

## Configuration

Main settings are in `src/main/resources/application.yml`.

Environment variables:

- `JWT_SECRET` — secret for JWT signing (defaults to a dev value)
- `JWT_EXPIRATION_MS` — token TTL in ms (defaults to 86400000)

## Database

- H2 in-memory
- H2 console: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:todo`

## Testing

```bash
mvn test
```

Coverage:

- RestAssured API integration tests (auth, tasks, end-to-end scenarios)
- Unit tests for user and task services
- Validation and error handling checks
- 69 tests with full coverage

Tests run on a random port to avoid conflicts with 8080.

## Architecture

This project follows **YAGNI principles** with minimal complexity:

- **Unified DTOs**: Single `TaskRequest` for both create and update operations
- **Direct authentication**: `User` entity implements `UserDetails` directly
- **Simple task listing**: No pagination - returns tasks sorted by creation date
- **Dependency injection**: Proper `@Service` classes instead of static utilities
- **Focused testing**: Essential tests without enterprise-level complexity

## Tech Stack

- Spring Boot 3.5.6
- Spring Security + JWT
- Spring Data JPA (H2)
- JUnit 5, Mockito
- RestAssured

## License

MIT
