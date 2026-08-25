# AI DecisionHub (Full-Stack Project)

AI DecisionHub is an orchestration platform that executes the loop:

**Predict → Decide → Execute → Verify → Learn → Improve**

This repository contains:
- **backend/** → Spring Boot 3 (Java 21) API
- **frontend/** → Next.js (App Router + TypeScript + Tailwind) dashboard

---

## ✅ Implemented Scope

This build delivers a complete, runnable MVP+ implementation from your spec:

- Requirement Analyzer (intent/entities/constraints extraction)
- Task Decomposer (request → task graph chain)
- Model/Tool Router (registry + capability filtering)
- Decision Engine (Cost + Quality + Speed + Risk weighted scoring)
- Workflow execution (simulated execution per task)
- Verification engine (pass/fail + score + notes)
- Decision memory outcomes (prediction vs actual + adjustment hints)
- Core API endpoints from the document
- WebSocket status publishing (`/ws`, topic `/topic/requests/{id}`)
- Analytics endpoint for decision quality/cost/latency trends
- Next.js dashboard to submit requests, inspect traces, and monitor KPIs

---

## Tech Stack

- Frontend: Next.js 16 + TypeScript + Tailwind
- Backend: Spring Boot 3.3 + Java 21 + Spring Data JPA + WebSocket + Security
- DB runtime for local demo: **H2 (Postgres mode)**
- Postgres-ready profile: `application-postgres.yml`
- Redis-ready wiring via Spring Data Redis

> On this machine Docker/Postgres/Redis are not installed, so local verification was done with H2 fallback.

---

## API Endpoints

- `POST   /api/v1/requests`
- `GET    /api/v1/requests/{id}`
- `GET    /api/v1/requests/{id}/tasks`
- `GET    /api/v1/tasks/{id}/decision`
- `POST   /api/v1/decisions/{id}/approve`
- `GET    /api/v1/executions/{id}`
- `GET    /api/v1/executions/{id}/verify`
- `GET    /api/v1/tools`
- `POST   /api/v1/tools`
- `GET    /api/v1/analytics/decisions`
- WebSocket: `/ws` (`/topic/requests/{id}`)

---

## Local Run (Windows)

### 1) Backend

```bash
cd backend
cmd.exe /c "mvnw.cmd spring-boot:run"
```

Backend default URL: `http://localhost:8080`

### 2) Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend URL: `http://localhost:3000`

The frontend uses `NEXT_PUBLIC_API_BASE=http://localhost:8080` by default.

---

## Optional: PostgreSQL Profile

If you have PostgreSQL running:

```bash
cd backend
cmd.exe /c "mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=postgres"
```

Set environment vars if needed:
- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`

---

## Project Structure

```text
ai-decisionhub/
  backend/
    src/main/java/com/aidecisionhub/backend/
      controller/
      service/
      repository/
      entity/
      dto/
      config/
      model/
  frontend/
    src/app/
      page.tsx
      layout.tsx
      globals.css
```

---

## Notes

- This version intentionally uses **simulated execution** for safe deterministic demos.
- The architecture is ready for future MCP live execution, fallback rerouting, and adaptive weight updates.
