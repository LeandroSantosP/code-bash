# AGENTS - DevRoast Monorepo

## Scope and Priority (Codex 5.3)
- This file defines global instructions for the entire monorepo.
- Codex should also load sub-agents when working inside module folders:
  - `backend/AGENTS.md`
  - `frontend/AGENTS.md`
- Nearest-file priority applies: module AGENTS override root AGENTS for that module.

## Monorepo Structure
- `backend/`: Spring Boot API (Java 21, Maven, PostgreSQL)
- `frontend/`: React + TypeScript + Vite web app
- `infra/`: Docker Compose files for local/dev/prod environments

## Global Conventions
- Directory names use `snake_case`.
- Keep changes scoped to the module being edited; avoid cross-module refactors unless requested.
- Do not edit generated or dependency folders (`node_modules/`, `dist/`, `target/`, `.vite/`).
- Prefer small, reviewable diffs and preserve existing project conventions when touching legacy code.

## Collaboration and Git Hygiene
- Never revert user changes that are unrelated to the requested task.
- Never run destructive git commands (for example `reset --hard`) unless explicitly requested.
- Use Conventional Commits style when a commit is requested (for example `feat:`, `fix:`, `refactor:`, `docs:`).

## Quality Gates
- Frontend checks from repo root:
  - `npm run lint:frontend`
  - `npm run build:frontend`
- Backend checks from repo root:
  - `npm run test:backend`
  - `npm run build:backend`
- Prefer running only relevant checks for the module changed, then expand if needed.

## Product Context
- DevRoast provides code analysis with optional roast mode, severity-based feedback, improvement suggestions, and a shame leaderboard.
- Backend is the source of truth for domain rules and API contracts.
- Frontend should consume backend APIs without duplicating backend business logic.
