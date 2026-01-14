# Kotlin Backend Skeleton

A robust, production-ready backend skeleton built with **Kotlin** and **Ktor**. This project serves as a starting point for building RESTful APIs, featuring a layered architecture, database integration with Exposed (SQL mainly), and JWT-based authentication.

## 🚀 Purpose

The goal of this project is to provide a clean, scalable, and easy-to-fork foundation for Kotlin backend development. It implements common patterns and best practices so developers can focus on business logic rather than boilerplate setup.

## 🚧 Under construction

**Note: The initiative is still under construction and will be updated regularly. Be aware that the initial setup contains some known vulnerabilities**

## 🏗 Architecture

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
*   **Language:** Kotlin (JVM 21)
*   **Framework:** Ktor 2.x (Server)
*   **Database:** PostgreSQL (it will be configurable soon)
*   **ORM:** JetBrains Exposed
*   **Connection Pool:** HikariCP
*   **Authentication:** JWT (JSON Web Tokens) & BCrypt
*   **Serialization:** Kotlinx Serialization
*   **Build Tool:** Gradle (Kotlin DSL)

## 📋 Prerequisites

*   **JDK 21** or higher.
*   **Docker** (for running the PostgreSQL database).
*   **IntelliJ IDEA** (Recommended IDE).

## 🛠 Database Setup (Docker)

You can spin up a PostgreSQL instance quickly using Docker. Run the following command in your terminal:

```bash
docker run --name kotlin-skeleton-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=kotlinskeletondb \
  -p 5432:5432 \
  -d postgres:15-alpine
```

*   **User:** `postgres`
*   **Password:** `postgres`
*   **Database Name:** `kotlinskeletondb`
*   **Port:** `5432`

## ⚙️ Configuration (Environment Variables)

The application is configured using `application.conf` which reads from Environment Variables. You must set these variables before running the application (e.g., in IntelliJ Run Configuration or your shell).
 
⚠️ **Note: Remember that this is just the foundation of a real backend system. In order to release this to production, you must change all these parameters.**

| Variable | Description | Example Value |
| :--- | :--- | :--- |
| `PORT` | Server port | `1234` |
| `HOST` | Server host | `0.0.0.0` |
| `DB_URL` | JDBC Connection URL | `jdbc:postgresql://localhost:5432/kotlinskeletondb` |
| `DB_USER` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |
| `DB_DRIVER` | Database driver class | `org.postgresql.Driver` |
| `JWT_SECRET` | Secret key for signing tokens | `my-super-secret-key` |
| `JWT_ISSUER` | Token issuer claim | `kotlin-backend` |
| `JWT_AUDIENCE` | Token audience claim | `backend-users` |

## 🏃‍♂️ How to Run

1.  Ensure your Docker database is running.
2.  Set the Environment Variables listed above.
3.  Run the application using Gradle:

```bash
./gradlew run
```

Or run the `main` function in `Application.kt` via IntelliJ IDEA.

## 📡 API Usage Examples (CURL)

Here are examples of how to interact with the API using `curl`.

### 1. Health Check
Check if the server and database are running.

```bash
curl -i -X GET http://localhost:1234/api/health_check
```

### 2. Register a User
Create a new account.

```bash
curl -i -X POST http://localhost:1234/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "username": "cooluser",
    "password": "securePassword123"
  }'
```

### 3. Login
Authenticate and receive a JWT Token.

```bash
curl -i -X POST http://localhost:1234/api/auth/login \
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
  "user": { user data}
}
```

### 4. Delete User (Protected Route)
Delete the currently authenticated user. Replace `<YOUR_TOKEN>` with the token received from login.

```bash
curl -i -X DELETE http://localhost:1234/api/auth/delete_user \
  -H "Authorization: Bearer <YOUR_TOKEN>"
```

## 📂 Project Structure

```
app/src/main/kotlin/com/decksolutions/kotlinbackendskeleton/
├── Application.kt          # Entry point & DI wiring
├── databases/              # Database configuration
├── models/
│   ├── entities/           # Database tables & Entity classes
│   ├── requests/           # Request DTOs
│   └── responses/          # Response DTOs
├── repositories/           # Database access logic
├── routes/                 # API Route definitions
├── services/               # Business logic
└── utils/                  # Utility classes
```
