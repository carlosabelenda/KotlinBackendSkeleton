# Kotlin Backend Skeleton

A robust, production-ready backend skeleton built with **Kotlin** and **Ktor**. This project serves as a starting point for building RESTful APIs, featuring a layered architecture, database integration with Exposed (SQL mainly), and JWT-based authentication.

## Purpose

The goal of this project is to provide a clean, scalable, and easy-to-fork foundation for Kotlin backend development. It implements common patterns and best practices so developers can focus on business logic rather than boilerplate setup.

## Under construction

**Note: The initiative is still under construction and will be updated regularly. Be aware that the initial setup contains some known vulnerabilities**

## Architecture

The project follows a **Layered Architecture** (Controller-Service-Repository pattern) to ensure separation of concerns and testability:

1.  **Routes (Presentation Layer):** Handles HTTP requests and responses. It delegates business logic to Services.
    *   Located in: `com.decksolutions.kotlinbackendskeleton.routes`
2.  **Services (Business Logic Layer):** Contains the core business rules. It orchestrates data flow between the Controller and Repository.
    *   Located in: `com.decksolutions.kotlinbackendskeleton.services`
3.  **Repositories (Data Access Layer):** Manages direct interaction with the database using **Exposed** (ORM).
    *   Located in: `com.decksolutions.kotlinbackendskeleton.repositories`
4.  **Models/Entities:** Defines the data structures for API responses (DTOs) and Database tables.
    *   Located in: `com.decksolutions.kotlinbackendskeleton.models`

### Key Technologies

| Technology | Version | Description |
|------------|---------|-------------|
| **Kotlin** | 2.2.20 | Programming language |
| **Ktor** | 3.3.3 | Web framework |
| **JVM** | 21 | Runtime |
| **PostgreSQL** | 15 | Database |
| **Exposed** | 0.44.1 | ORM |
| **HikariCP** | 7.0.2 | Connection pool |
| **JWT** | - | Authentication |
| **BCrypt** | 0.4 | Password hashing |
| **Kotlinx Serialization** | - | JSON serialization |
| **OpenAPI/Swagger** | 5.4.0 | API documentation |

## Prerequisites

*   **JDK 21** or higher
*   **Docker & Docker Compose** (for containerized deployment)
*   **IntelliJ IDEA** (Recommended IDE)

## Quick Start with Docker Compose

The easiest way to run the application is using Docker Compose, which starts both the PostgreSQL database and the backend application.

```bash
# Build and start all services
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Stop and remove volumes (deletes database data)
docker-compose down -v
```

Once running, the application is available at:
- **API**: http://localhost:1234
- **Swagger UI**: http://localhost:1234/swagger
- **OpenAPI Spec**: http://localhost:1234/openapi.json

## API Documentation (OpenAPI/Swagger)

The API is fully documented using OpenAPI 3.0 specification with interactive Swagger UI.

### Accessing the Documentation

| URL | Description |
|-----|-------------|
| `/swagger` | Interactive Swagger UI for testing endpoints |
| `/openapi.json` | OpenAPI specification in JSON format |

### Available Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `GET` | `/api/health_check` | System health status | No |
| `POST` | `/api/auth/register` | Register new user | No |
| `POST` | `/api/auth/login` | User login | No |
| `DELETE` | `/api/auth/delete_user` | Delete authenticated user | JWT |
| `GET` | `/echo` | Simple health endpoint | No |

### JWT Authentication

Protected endpoints require a valid JWT token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

In Swagger UI, click the "Authorize" button and enter your token to test protected endpoints.

## Configuration

### Environment Variables

The application is configured via environment variables. When using Docker Compose, default values are provided.

| Variable | Description | Default |
|----------|-------------|---------|
| `PORT` | Server port | `1234` |
| `HOST` | Server host | `0.0.0.0` |
| `DB_URL` | JDBC Connection URL | `jdbc:postgresql://postgres:5432/kotlinskeletondb` |
| `DB_USER` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |
| `DB_DRIVER` | Database driver class | `org.postgresql.Driver` |
| `JWT_SECRET` | Secret key for signing tokens | `your-secret-key-change-in-production` |
| `JWT_ISSUER` | Token issuer claim | `your-issuer` |
| `JWT_AUDIENCE` | Token audience claim | `your-audience-target` |

**Warning:** Change all default values before deploying to production!

### Custom Configuration

Create a `.env` file in the project root to override defaults:

```env
DB_NAME=mydb
DB_USER=myuser
DB_PASSWORD=mysecretpassword
JWT_SECRET=my-production-secret-key
JWT_ISSUER=my-app
JWT_AUDIENCE=my-users
```

## Running Locally (Without Docker)

### 1. Start PostgreSQL

```bash
docker run --name kotlin-skeleton-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=kotlinskeletondb \
  -p 5432:5432 \
  -d postgres:15-alpine
```

### 2. Set Environment Variables

```bash
export DB_URL=jdbc:postgresql://localhost:5432/kotlinskeletondb
export DB_USER=postgres
export DB_PASSWORD=postgres
export DB_DRIVER=org.postgresql.Driver
export JWT_SECRET=my-secret-key
export JWT_ISSUER=my-issuer
export JWT_AUDIENCE=my-audience
```

### 3. Run the Application

```bash
./gradlew :app:run
```

Or run the `main` function in `Application.kt` via IntelliJ IDEA.

## Testing

The project includes both unit tests and integration tests.

### Running Tests

```bash
# Run all tests
./gradlew test

# Run only app module tests
./gradlew :app:test

# Run tests with verbose output
./gradlew test --info
```

### Test Structure

| Type | Location | Description |
|------|----------|-------------|
| Unit Tests | `app/src/test/kotlin/.../services/` | Service layer tests with mocked dependencies |
| Integration Tests | `app/src/test/kotlin/.../repositories/` | Repository tests with H2 in-memory database |

### Test Technologies

- **JUnit 4** - Test framework
- **MockK** - Mocking library for Kotlin
- **H2 Database** - In-memory database for integration tests
- **Ktor Test Host** - Testing Ktor applications

### Example: Running a Specific Test

```bash
./gradlew :app:test --tests "UserServiceTest"
```

## API Usage Examples (cURL)

### Health Check

```bash
curl -X GET http://localhost:1234/api/health_check
```

### Register a User

```bash
curl -X POST http://localhost:1234/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "username": "cooluser",
    "password": "securePassword123"
  }'
```

### Login

```bash
curl -X POST http://localhost:1234/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "securePassword123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 1,
    "email": "user@example.com"
  }
}
```

### Delete User (Protected)

```bash
curl -X DELETE http://localhost:1234/api/auth/delete_user \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

## Project Structure

```
KotlinBackendSkeleton/
├── app/
│   └── src/
│       ├── main/kotlin/com/decksolutions/kotlinbackendskeleton/
│       │   ├── Application.kt          # Entry point & DI wiring
│       │   ├── config/                 # Configuration (OpenAPI)
│       │   ├── databases/              # Database configuration
│       │   ├── models/
│       │   │   ├── entities/           # Database tables & Entity classes
│       │   │   ├── requests/           # Request DTOs
│       │   │   └── responses/          # Response DTOs
│       │   ├── repositories/           # Database access logic
│       │   ├── routes/                 # API Route definitions
│       │   └── services/               # Business logic
│       └── test/kotlin/                # Unit & Integration tests
├── utils/                              # Shared utilities module
├── docker-compose.yml                  # Docker Compose configuration
├── Dockerfile                          # Multi-stage Docker build
└── .dockerignore                       # Docker build exclusions
```

## Docker Configuration

### Dockerfile

The project uses a multi-stage build for optimized image size:

1. **Build stage**: Uses `gradle:8.5-jdk21` to compile the application
2. **Runtime stage**: Uses `eclipse-temurin:21-jre-alpine` for minimal runtime

### Docker Compose Services

| Service | Container Name | Port | Description |
|---------|---------------|------|-------------|
| `postgres` | kotlin-skeleton-db | 5432 | PostgreSQL database |
| `backend` | kotlin-skeleton-container | 1234 | Ktor application |

### Useful Docker Commands

```bash
# View running containers
docker-compose ps

# View backend logs
docker-compose logs -f backend

# Restart only backend (after code changes)
docker-compose up -d --build backend

# Access PostgreSQL CLI
docker exec -it kotlin-skeleton-db psql -U postgres -d kotlinskeletondb

# Remove all containers and volumes
docker-compose down -v
```