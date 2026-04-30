# Todo List Backend API

A production-ready REST API built with Spring Boot 3.5.6 and Java 17, featuring JWT-based authentication, role-based access control, and per-user data isolation.

## 🛠 Tech Stack

- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17 (LTS)
- **Security**: Spring Security 6.x, JWT (jjwt 0.12.6), BCrypt
- **Database**: H2 (in-memory), JPA/Hibernate
- **Documentation**: SpringDoc OpenAPI 2.6.0, Swagger UI 5.x
- **Tools**: Lombok, Jackson, Maven
- **Testing**: JUnit 5.11.3, Mockito, RestAssured 5.5.0, AssertJ 3.26.3, Spring Boot Test

## ✨ Core Features

- **JWT Authentication**
  - Secure registration & login endpoints
  - 24-hour default token expiration (configurable)
  - Stateless session management
  - HS256 algorithm with 256-bit secret key

- **Role-Based Access Control (RBAC)**
  - `USER` and `ADMIN` roles with method-level security
  - `@Secured` and `@PreAuthorize` annotations
  - Fine-grained endpoint protection

- **Task Management**
  - Full CRUD operations for todo items
  - Per-user data isolation via `userId` foreign key
  - Default values: `completed: false`, `createdAt: NOW`
  - Input validation with Bean Validation annotations

- **Layered Architecture**
  - Clean separation: Controller → Service → Repository
  - DTO pattern for request/response objects
  - Global exception handling with `@ControllerAdvice`
  - Centralized error responses with proper HTTP status codes

## 🚀 How to Run

### Prerequisites
- Java 17+ (`java -version`)
- Maven 3.8+ (`mvn -v`)

### Build & Run

```bash
# Clean and build the project
mvn clean install

# Run the application
mvn spring-boot:run

# Or use the Maven wrapper (if available)
./mvnw clean spring-boot:run
```

The application starts on **`http://localhost:8080`** by default.

## 🔐 Security & JWT Usage

### Token Configuration
- **Default Expiration**: 24 hours (86,400,000 ms)
- **Secret**: 256-bit Base64-encoded key (set via `JWT_SECRET` env var)
- **Algorithm**: HS256

### Authentication Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | User registration |
| POST | `/api/auth/login` | JWT token generation |

### Secured Endpoints
All endpoints under `/api/**` (except `/api/auth/**`) require a valid JWT.

**Bearer Token Format:**
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzQ0...
```

**Role Mapping:**
- `ROLE_USER` → Standard user operations (own tasks)
- `ROLE_ADMIN` → User + admin operations (all tasks, user management)

## 📚 API Documentation

### Swagger UI
Open in your browser:
```
http://localhost:8080/swagger-ui.html
```

The standalone `swagger-ui.html` page is configured to fetch the OpenAPI specification from SpringDoc's default endpoint (`/v3/api-docs`). The UI includes:
- Interactive API testing
- Authentication setup via "Authorize" button
- Request/response examples
- Schema documentation

**Note**: SpringDoc OpenAPI dependency is already included in `pom.xml`. The `/v3/api-docs` endpoint will be available automatically.

### Base Path
All API routes are prefixed with `/api`.

### Example Request
```bash
# Get all tasks (requires auth)
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer <your_jwt_token>"

# Create a new task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -d '{"title":"Learn Spring Boot","description":"Complete the tutorial","dueDate":"2024-12-31"}'
```

## ⚙️ Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | Base64-encoded 256-bit secret | *auto-generated* |
| `JWT_EXPIRATION_MS` | Token lifetime in milliseconds | `86400000` |
| `H2_CONSOLE_ENABLED` | Enable H2 web console | `true` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `default` |

### H2 Console
When enabled, access the database at:
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:tododb
User:     sa
Password: (leave empty)
```

## 📁 Project Structure

```
src/main/java/com/example/todo/
├── config/            # Spring configuration classes
├── security/          # JWT filter, SecurityConfig, UserDetailsService
├── controller/        # REST endpoints (@RestController)
├── service/           # Business logic (@Service)
├── repository/        # Data access layer (@Repository, JPA)
├── entity/            # JPA entities (Task, User)
├── dto/               # Request/Response DTOs
│   ├── request/       # Incoming payload classes
│   └── response/      # Outgoing payload classes
└── exception/         # Global exception handler (@ControllerAdvice)
```

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Test Coverage
- **Unit Tests**: Service & Repository layers (JUnit 5, Mockito)
- **Integration Tests**: Controller layer with `@WebMvcTest`
- **API Tests**: RestAssured-based contract tests
- **Security Tests**: JWT authentication and authorization

### Test Examples
```bash
# Run only unit tests
mvn test -Dtest="*Test"

# Run only integration tests
mvn test -Dtest="*IT"

# Run tests with specific profile
mvn test -Dspring.profiles.active=test

# Run with coverage report (if JaCoCo configured)
mvn clean test jacoco:report
```

### Test Structure
- `src/test/java/com/example/todo/`
  - `controller/` - REST endpoint tests
  - `service/` - Business logic tests
  - `repository/` - Data access tests
  - `security/` - Authentication/authorization tests

## 🤝 Next Steps / Roadmap

- [ ] **PostgreSQL Migration** — Replace H2 with production-ready RDBMS
- [ ] **Refresh Tokens** — Long-lived refresh + short-lived access tokens
- [ ] **Audit Logging** — Entity change tracking with `@CreatedDate`, `@LastModifiedDate`
- [ ] **Dockerization** — Multi-stage Dockerfile + Docker Compose
- [ ] **CI/CD Pipeline** — GitHub Actions with Maven, JUnit, and security scanning
- [ ] **Rate Limiting** — API endpoint protection against abuse
- [ ] **Caching** — Redis integration for performance optimization
- [ ] **API Versioning** — Support for multiple API versions


