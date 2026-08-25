# 🧠 AI DecisionHub

**Intelligent decision-making platform** — analyze options, compare trade-offs, simulate what-if scenarios, and get AI-powered recommendations. Built with a deterministic scoring engine (no fake numbers) and a multi-agent AI pipeline for reasoning and explanations.

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────┐
│                   AI DecisionHub                    │
├──────────┬────────────────┬─────────────────────────┤
│ Frontend │    Backend     │       AI Service         │
│ React 18 │  Spring Boot 3 │  FastAPI + LangGraph     │
│ TypeScript│   Java 21     │  Deterministic Engine    │
│ Vite     │  H2 / MySQL   │  Multi-Agent Pipeline    │
│ Tailwind │  JWT Auth     │  Ollama / Gemini Fallback│
│ Framer   │  REST API     │                         │
│ Recharts │  Swagger      │                         │
└──────────┴────────────────┴─────────────────────────┘
```

**Golden rule:** All numeric scores come from a deterministic math engine. The AI only produces reasoning/explanation text — it never invents numbers.

---

## 🚀 Quick Start

### Option 1: Docker (all services)
```bash
docker-compose up -d
# Frontend: http://localhost:3000
# Backend:  http://localhost:8080
# AI:       http://localhost:8000
```

### Option 2: Manual (for development)

#### Prerequisites
- Java 21+
- Node.js 20+
- Python 3.12+
- Maven (or use Maven Wrapper)

#### Backend
```bash
cd backend
mvn spring-boot:run
# Runs on http://localhost:8080
# H2 Console: http://localhost:8080/h2-console
```

#### AI Service
```bash
cd ai-service
pip install -r requirements.txt
python main.py
# Runs on http://localhost:8000
# Docs: http://localhost:8000/docs
```

#### Frontend
```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:5173
```

### Demo Credentials
```
Username: user
Password: password
```
Sample data is seeded automatically on first run.

---

## 📋 API Documentation

### Backend (Spring Boot)
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:decisionhub`)

### AI Service (FastAPI)
- OpenAPI docs: `http://localhost:8000/docs`

### Key Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register user |
| POST | `/api/auth/login` | Login (returns JWT) |
| GET | `/api/decisions` | List user's decisions |
| POST | `/api/decisions` | Create decision |
| GET | `/api/decisions/{id}/comparison` | Run scoring engine |
| POST | `/api/decisions/{id}/simulate` | What-if simulation |
| POST | `/api/decisions/{id}/analyze` | Trigger AI analysis |
| GET | `/api/analytics/dashboard` | Dashboard stats |
| GET | `/api/analytics/insights` | Historical insights |

---

## 📁 Project Structure

```
ai-decisionhub/
├── frontend/          # React + TypeScript + Vite + Tailwind
│   ├── src/
│   │   ├── components/   # Reusable UI components
│   │   ├── pages/        # Route pages
│   │   ├── layouts/      # Layout components
│   │   ├── services/     # API client + service modules
│   │   ├── hooks/        # Custom React hooks
│   │   ├── context/      # Auth + Theme context
│   │   ├── animations/   # Framer Motion variants
│   │   ├── charts/       # Recharts wrappers
│   │   └── types/        # TypeScript interfaces
│   └── ...
├── backend/           # Spring Boot + JPA + Security
│   └── src/main/java/com/decisionhub/
│       ├── controller/   # REST endpoints
│       ├── service/      # Business logic + scoring engine
│       ├── repository/   # JPA repositories
│       ├── entity/       # JPA entities
│       ├── dto/          # Request/response DTOs
│       ├── security/     # JWT + Spring Security
│       ├── config/       # CORS + seed data
│       └── exception/    # Global error handler
├── ai-service/        # FastAPI + deterministic AI
│   ├── main.py           # API server + agent logic
│   └── requirements.txt
├── database/
│   └── schema.sql        # Full MySQL schema
├── docker-compose.yml
└── README.md
```

---

## 🧪 AI Pipeline

The AI service implements a 5-agent pipeline:

1. **Research Agent** — Gathers context and data about each option
2. **Analysis Agent** — Evaluates options against weighted factors
3. **Risk Agent** — Identifies risks, uncertainties, and mitigation
4. **Comparison Agent** — Head-to-head option comparison
5. **Decision Agent** — Synthesizes findings into final recommendation

Each agent produces reasoning text only. All numeric scores come from the backend's deterministic scoring engine.

---

## ⚙️ Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:h2:mem:decisionhub` | Database URL |
| `JWT_SECRET` | (in application.yml) | JWT signing secret |
| `CORS_ORIGINS` | `http://localhost:5173` | Allowed CORS origins |
| `AI_MODEL` | `deterministic` | AI model (deterministic/ollama/gemini) |
| `GEMINI_API_KEY` | — | Optional Gemini API key |
| `OLLAMA_HOST` | `http://localhost:11434` | Ollama host for local LLM |

---

## 🎨 Design System

- **Framework:** Tailwind CSS with `indigo` accent
- **Animations:** Framer Motion (page transitions, card stagger, score counters, agent network)
- **Charts:** Recharts (radar, line, bar, pie)
- **Icons:** Lucide React
- **Dark Mode:** System preference + manual toggle
- **Responsive:** Mobile-first, 320px → large desktop, no horizontal scroll

---

## 📄 License

MIT — built for educational and demonstration purposes.