# family-health-api

Spring Boot 3 REST API for managing children's health records, milestones, and reminders per authenticated user.

## Tech Stack

- **Java 21**, **Spring Boot 3.3.4**, **Maven**
- **Spring Security** with **JWT** (stateless, no sessions)
- **Spring Data JPA** + **PostgreSQL** (dev) / **H2** (test)
- **Liquibase** for all schema management
- **OpenAPI 3** spec with **delegate pattern** code generation (`openapi-generator-maven-plugin`)
- **Lombok**

## Project Structure

```
src/main/java/com/familyhealth/api/
├── config/          # SecurityConfig, JwtConfig
├── controller/      # Delegate implementations only (e.g. ChildApiDelegateImpl)
├── service/         # Business logic
├── repository/      # Spring Data JPA repositories
├── model/           # JPA entities
└── security/        # JwtAuthFilter, UserDetailsServiceImpl

src/main/resources/
├── openapi/         # family-health-api.yaml — the API contract (source of truth)
├── db/changelog/    # Liquibase changelogs
│   └── changes/     # Numbered changesets: 001-..., 002-..., etc.
├── application.yml          # Shared config
├── application-dev.yml      # PostgreSQL
└── application-test.yml     # H2 in-memory
```

## Key Conventions

### API & Controllers
- The OpenAPI spec (`src/main/resources/openapi/family-health-api.yaml`) is the **source of truth** — define endpoints there first
- Controllers are **delegate implementations only** — never write `@RestController` classes manually; implement the generated `*ApiDelegate` interface instead
- Generated code lives in `target/generated-sources/openapi/` — never edit it directly

### Database
- **Liquibase owns all schema changes** — never use `ddl-auto: create`, `update`, or `create-drop` in any profile
- `ddl-auto` is set to `validate` (shared config) and `none` (test)
- New changesets go in `src/main/resources/db/changelog/changes/` numbered sequentially
- Changeset SQL must be **ANSI-compatible** so both H2 and PostgreSQL can run the same scripts

### Profiles
- `dev` — PostgreSQL at `localhost:5432/family_health`
- `test` — H2 in-memory; activated automatically during `mvn test`
- Run with: `SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run`

### Security
- All endpoints require a valid JWT except `POST /auth/register` and `POST /auth/login`
- JWT secret is read from the `JWT_SECRET` environment variable (never hardcode it)

## Running the App

```bash
# Dev (requires PostgreSQL running locally)
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run

# Tests (uses H2, no external DB needed)
mvn test

# Full build
mvn clean install
```

## Swagger UI

Available at `http://localhost:8080/api/v1/swagger-ui.html` when running locally.
