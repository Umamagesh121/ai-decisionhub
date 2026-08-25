# Verification Evidence

## Backend

Compiled successfully:

```bash
cd backend
cmd.exe /c "mvnw.cmd -DskipTests compile"
```

Runtime checks:

- `GET /actuator/health` → `{"status":"UP"}`
- `GET /api/v1/tools` → 4 seeded tools
- `POST /api/v1/requests` (multiple runs) → `DONE` with verifications passed
- `GET /api/v1/tasks/{id}/decision` → decision + ranked candidates
- `GET /api/v1/analytics/decisions` → aggregated metrics

## Frontend

Build successful:

```bash
cd frontend
npm run build
npm run lint
```

Production server check:

```bash
npm run start -- --port 3100
curl -i http://127.0.0.1:3100
```

Response includes:
- `<title>AI DecisionHub</title>`
- Dashboard UI content (request form, tools, analytics sections)
