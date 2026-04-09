# AGENTS - Backend (Spring Boot)

## Scope
- Applies to all files under `backend/`.
- Overrides root instructions when working in this module.

## Stack and Runtime
- Java 21.
- Spring Boot 4.x with Maven Wrapper (`./mvnw`).
- PostgreSQL 16 with Flyway migrations.
- Spring Data JDBC (no JPA/Hibernate patterns unless requested).

## Architecture and Folder Conventions
- Base package: `com.leandrosps.bug_bash`.
- Keep package names lowercase and use domain-oriented organization under `app/` and `config/`.
- Follow current layered approach:
  - `app/`: controllers, use cases/services, repositories, entities.
  - `config/`: Spring configuration, security, and infrastructure beans.
  - `entriesobj/`: API payload/DTO helper records or value objects.
- Database changes must go in `src/main/resources/db/migration/` with sequential Flyway versions.

## API Design Conventions
- Keep controllers thin: validate inputs, delegate business rules to services/use cases.
- Use explicit request/response DTOs for API boundaries; avoid exposing persistence entities directly.
- Maintain stable JSON contracts and consistent error responses through global exception handling.
- Preserve severity semantics (`critical`, `warning`, `good`) and roast-mode behavior from product context.

## Spring and Java Best Practices
- Prefer constructor injection; avoid field injection.
- Use Bean Validation (`jakarta.validation`) annotations for request validation.
- Keep methods small and side effects explicit.
- Use Lombok only where it improves readability and is already consistent in the file.
- Avoid introducing reflection-heavy or magic frameworks unless there is a clear need.

## Security and Data Rules
- Keep OAuth2/JWT and Spring Security configurations coherent with existing `config/` classes.
- Never hardcode secrets, tokens, or credentials; use environment variables/properties.
- Do not log sensitive user data, tokens, or full credentials.
- Principle of least privilege for endpoints and DB access.

## Testing and Verification
- Prefer focused tests for changed behavior first, then broader suite if needed.
- Main commands (from repo root):
  - `npm run test:backend`
  - `npm run build:backend`
- Alternative direct module commands:
  - `./mvnw -f backend/pom.xml test`
  - `./mvnw -f backend/pom.xml clean package`

## Do Not Touch
- Generated artifacts and dependency folders (`target/`, local caches).
- Unrelated modules (`frontend/`, `infra/`) unless explicitly requested.
