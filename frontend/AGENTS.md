# AGENTS - Frontend (React)

## Scope
- Applies to all files under `frontend/`.
- Overrides root instructions when working in this module.

## Stack and Runtime
- React 19 + TypeScript + Vite.
- ESLint and Biome available; keep current formatting/lint style unless instructed otherwise.
- Tailwind CSS v4 utilities are available (`tailwind-merge`, `tailwind-variants`).

## Project Structure Conventions
- Keep source files under `src/`.
- Current structure is intentionally small:
  - `src/lib/`: shared utilities and API client code.
  - `src/`: app entry (`main.tsx`), root component (`App.tsx`), and styles.
- Prefer co-locating feature-specific code as the app grows (component + styles + tests together).

## React + TypeScript Community Conventions
- Use functional components and hooks.
- Keep components focused and composable; extract reusable logic into hooks/utilities.
- Prefer explicit TypeScript types at public boundaries (props, API payloads, utility contracts).
- Avoid `any`; use `unknown` + narrowing when needed.
- Keep UI state local first; lift state only when shared behavior requires it.

## UI and UX Guidelines
- Preserve existing visual language unless a redesign is requested.
- Build responsive layouts that work on mobile and desktop.
- Keep accessibility basics in place: semantic HTML, keyboard reachability, labels, and contrast.
- Handle loading, error, and empty states for async flows.

## API and Data Boundaries
- Frontend consumes backend APIs; do not duplicate backend business rules in UI.
- Keep API integration code centralized in `src/lib/api.ts` (or adjacent service modules).
- Normalize/transform server data at the boundary layer, not deep inside UI components.

## Quality and Verification
- Main commands (from repo root):
  - `npm run lint:frontend`
  - `npm run build:frontend`
- Alternative direct module commands:
  - `npm --prefix frontend run lint`
  - `npm --prefix frontend run build`

## Do Not Touch
- Generated and dependency folders (`dist/`, `node_modules/`, `.vite/`).
- Unrelated modules (`backend/`, `infra/`) unless explicitly requested.
