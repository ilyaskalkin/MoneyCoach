# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MoneyCoach is a Spring Boot REST API for tracking and managing personal finance operations. It uses a layered architecture: Controller → Service → Repository → JPA Entity.

## Build & Run Commands

```bash
# Build
mvn clean install

# Run (production — requires PostgreSQL)
mvn spring-boot:run

# Run with in-memory H2 database (development)
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=OperationServiceTest

# Package JAR
mvn package -DskipTests
```

## Architecture

**Package:** `ru.iskalkin.moneycoach`

- `model/Operation.java` — JPA entity with fields: id, date, amount, kind, account, description
- `repository/OperationRepository.java` — Spring Data JPA; provides query methods (findByDateBetween, findByKind, etc.)
- `service/OperationService.java` — Business logic; search filtering is done in-memory via Java streams after DB fetch
- `controller/OperationController.java` — REST endpoints under `/api/operations`; still uses entities directly (DTO migration is a TODO)
- `dto/OperationSearchRequest.java` — Filter object for the search endpoint (date range, kind, account, description)

## Database

- **Production:** PostgreSQL (configured in `application.yml`)
- **Development:** H2 in-memory with PostgreSQL compatibility mode (`application-dev.yml`); H2 console at `/h2-console`
- `data.sql` seeds the `operations` and `kind` tables on startup

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/health` | Health check |
| GET | `/api/operations/all` | Get all operations |
| POST | `/api/operations/add` | Create operation (returns 201) |
| DELETE | `/api/operations/delete/{id}` | Delete operation (returns 204) |
| POST | `/api/operations/find` | Search with filters |

Swagger UI: `/swagger-ui.html` — API spec also available at `/v3/api-docs` and in `src/main/resources/swagger.json`.

## Key Notes

- Java 22 source, compiled to Java 16 bytecode
- Lombok is used throughout (`@Data`, `@RequiredArgsConstructor`, `@Slf4j`, `@Builder`)
- The `OperationController` has a TODO to replace entity usage with DTOs in the API layer
