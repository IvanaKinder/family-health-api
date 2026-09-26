# Family Health API

REST API for managing children's health records, milestones, and reminders per authenticated user.

## Tech Stack

- **Java 21** / **Spring Boot 3.3.4** / **Maven**
- **Spring Security** — stateless JWT authentication
- **Spring Data JPA** + **PostgreSQL**
- **Liquibase** — database schema management
- **OpenAPI 3** — contract-first with delegate pattern code generation
- **MapStruct** — DTO ↔ entity mapping
- **Lombok**

## Prerequisites

- Java 21
- Maven 3.9+
- Docker (for running PostgreSQL)

## Running Locally

### 1. Start PostgreSQL

PostgreSQL is orchestrated from the companion [family-health-compose](https://github.com/IvanaKinder/family-health-compose) repo:

```bash
cd ../family-health-compose
docker-compose up -d postgres
```

### 2. Start the API

```bash
cd family-health-api
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

### 3. Run Tests

Tests use an in-memory H2 database — no external dependencies needed:

```bash
mvn test
```

## API Documentation

Swagger UI is available at `http://localhost:8080/swagger-ui/index.html` when the app is running.

The OpenAPI spec is the source of truth and lives at `src/main/resources/openapi/family-health-api.yaml`.

## Running the Full Stack (Demo)

To run the API together with the database and frontend in Docker:

```bash
cd ../family-health-compose
docker-compose up --build
```

See the [family-health-compose](https://github.com/IvanaKinder/family-health-compose) repo for details.

## Environment Variables

| Variable | Default | Description |
|---|---|---|
| `JWT_SECRET` | `change-me-in-production-must-be-at-least-32-chars!!` | Secret key for signing JWTs |
| `DB_USERNAME` | `postgres` | PostgreSQL username |
| `DB_PASSWORD` | `postgres` | PostgreSQL password |

## Spring Profiles

| Profile | Database | Notes |
|---|---|---|
| `dev` | PostgreSQL at `localhost:5432` | Used for local development |
| `test` | H2 in-memory | Activated automatically by `mvn test` |
| `docker` | PostgreSQL at `postgres:5432` | Used inside Docker Compose |
