# DevRoast Monorepo

Monorepo containing the DevRoast backend API and frontend web app.

## Structure

```
backend/                      # Spring Boot API (Java 21)
frontend/                     # React + Vite + TypeScript app
infra/                        # Docker Compose files (dev/prod)
meu-projeto.code-workspace    # VS Code multi-root workspace
package.json                  # Root npm scripts for frontend/backend
pom.xml                       # Root Maven aggregator (module: backend)
```

## Prerequisites

- Java 21+
- Node.js 22+
- npm 10+
- Docker (for PostgreSQL and Ollama, if using compose)

## Backend

Run backend locally:

```bash
cd backend
./mvnw spring-boot:run
```

Run backend tests:

```bash
cd backend
./mvnw test
```

Start full development stack with Docker Compose:

```bash
docker compose -f infra/docker-compose.dev.yml up -d
```

Start production-like stack with Docker Compose:

```bash
docker compose -f infra/docker-compose.prod.yml up -d
```

## Frontend

Install and run frontend:

```bash
cd frontend
npm install
npm run dev
```

## Root Scripts

From the repository root:

```bash
npm run dev:frontend
npm run dev:backend
npm run build:frontend
npm run test:backend
npm run docker:dev:up
npm run docker:dev:down
npm run docker:prod:up
npm run docker:prod:down
```

## Build Monorepo Maven Module

From the repository root:

```bash
mvn -pl backend clean package
```
