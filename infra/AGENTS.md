# AGENTS - Infra (Docker Compose)

## Scope
- Applies to all files under `infra/`.
- Overrides root instructions when working in this module.

## Infrastructure Context
- This module defines local/dev/prod orchestration through Docker Compose.
- Current services: `postgres`, `ollama`, `backend`, `frontend`.
- Compose files:
  - `docker-compose.dev.yml`
  - `docker-compose.prod.yml`

## Docker Compose Conventions
- Keep service names stable; downstream dependencies and env references rely on them.
- Use explicit image tags (for example `postgres:16`, `node:22-alpine`) and avoid floating latest tags.
- Keep environment variables centralized per service and prefer explicit values over hidden defaults.
- Prefer named volumes for persistent state (`postgres_data_*`, `ollama_data_*`).
- Maintain clear startup dependencies with `depends_on` and deterministic startup commands.

## Networking and Ports
- Keep published ports documented and intentional:
  - PostgreSQL: `5432`
  - Ollama: `11434`
  - Backend: `8080`
  - Frontend dev: `5173`
  - Frontend prod: `4173` (host) -> `80` (container)
- Avoid changing host ports unless required by conflict or explicit request.
- Preserve internal DNS usage via service names (for example `http://backend:8080`, `jdbc:postgresql://postgres:5432/bug_bash`).

## Environment and Secrets
- Never commit real secrets to compose files.
- Use `.env`/injected environment values for sensitive data in production-like setups.
- Keep dev defaults simple and local-only; document any required overrides.

## Health and Reliability
- Prefer resilient startup flows for dependent services (especially model/database readiness).
- Add or maintain healthchecks when changes make readiness assumptions fragile.
- Keep `restart` policies intentional and consistent with environment goals.

## Verification Commands
- From repo root:
  - `npm run docker:dev:up`
  - `npm run docker:dev:down`
  - `npm run docker:prod:up`
  - `npm run docker:prod:down`
- Direct compose alternatives:
  - `docker compose -f infra/docker-compose.dev.yml up -d`
  - `docker compose -f infra/docker-compose.prod.yml up -d`

## Do Not Touch
- Application source modules (`backend/`, `frontend/`) unless explicitly requested.
- Generated runtime artifacts and local Docker data outside versioned infra files.
