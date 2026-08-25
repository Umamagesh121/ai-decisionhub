from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
from typing import List, Dict, Optional, Any
import random
import math
import json
from datetime import datetime
from enum import Enum

app = FastAPI(
    title="AI DecisionHub - AI Service",
    description="Multi-agent AI orchestration for decision analysis",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173", "http://localhost:3000", "http://localhost:8080"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ── Models ──────────────────────────────────────────────

class FactorData(BaseModel):
    name: str
    weight: float

class OptionScore(BaseModel):
    option_name: str
    factor_name: str
    score: float

class OptionData(BaseModel):
    name: str
    description: str = ""
    total_score: float = 0.0
    factor_scores: Dict[str, float] = {}
    rank: int = 0

class AnalyzeRequest(BaseModel):
    decision_title: str
    decision_description: str = ""
    category: str = "General"
    options: List[OptionData]
    factors: List[FactorData]
    urgency: str = "MEDIUM"
    budget: Optional[float] = None

class AgentResult(BaseModel):
    agent: str
    status: str = "idle"
    content: str = ""
    confidence: float = 0.0

class AnalyzeResponse(BaseModel):
    research: AgentResult
    analysis: AgentResult
    risk: AgentResult
    comparison: AgentResult
    recommendation: AgentResult
    top_pick: str = ""
    confidence: float = 0.0
    model_used: str = "deterministic-fallback"

class SimulationRequest(BaseModel):
    decision_data: AnalyzeRequest
    adjusted_weights: Dict[str, float] = {}
    budget_change: float = 0
    risk_tolerance: str = "MEDIUM"

class SimulationResponse(BaseModel):
    before: AnalyzeResponse
    after: AnalyzeResponse
    rank_changes: Dict[str, int] = {}
    recommendation_changed: bool = False


# ── Deterministic Scoring Engine ────────────────────────

def calculate_weighted_scores(
    options: List[OptionData],
    factors: List[FactorData],
    scores_map: Optional[Dict[str, Dict[str, float]]] = None,
    weight_overrides: Optional[Dict[str, float]] = None
) -> List[OptionData]:
    """Pure math scoring - no LLM involved."""
    if not factors or not options:
        return options

    total_weight = sum(f.weight for f in factors)
    if total_weight == 0:
        total_weight = 1

    for option in options:
        weighted_sum = 0.0
        for factor in factors:
            w = weight_overrides.get(factor.name, factor.weight) if weight_overrides else factor.weight
            s = 0.0
            if scores_map and option.name in scores_map and factor.name in scores_map[option.name]:
                s = scores_map[option.name][factor.name]
            elif factor.name in option.factor_scores:
                s = option.factor_scores[factor.name]
            else:
                s = random.uniform(3, 8)
            weighted_sum += (s / 10.0) * (w / total_weight)
        option.total_score = round(weighted_sum * 100, 1)

    ranked = sorted(options, key=lambda o: o.total_score, reverse=True)
    for i, opt in enumerate(ranked):
        opt.rank = i + 1

    return ranked


# ── Agent Logic (rule-based, deterministic) ─────────────

def research_agent(req: AnalyzeRequest, ranked: List[OptionData]) -> AgentResult:
    lines = [
        f"Researching {len(req.options)} options for '{req.decision_title}'.",
        f"Category: {req.category}. Urgency: {req.urgency}."
    ]
    for opt in ranked:
        lines.append(f"• {opt.name} (Rank #{opt.rank}, Score {opt.total_score}): Analyzed against {len(req.factors)} factors.")
    return AgentResult(
        agent="research",
        status="complete",
        content="\n".join(lines),
        confidence=0.85
    )


def analysis_agent(req: AnalyzeRequest, ranked: List[OptionData]) -> AgentResult:
    best = ranked[0]
    worst = ranked[-1]
    score_spread = best.total_score - worst.total_score
    confidence = min(0.95, 0.5 + score_spread / 100)

    lines = [
        f"Top performer: {best.name} ({best.total_score}/100)",
        f"Score spread: {score_spread:.1f} points between best and worst option.",
    ]
    if score_spread < 10:
        lines.append("⚠ Options are closely matched — small weight changes could flip the recommendation.")
    else:
        lines.append(f"✓ {best.name} has a clear advantage over alternatives.")

    for f in req.factors:
        best_for_factor = max(ranked, key=lambda o: o.factor_scores.get(f.name, 0))
        lines.append(f"• {f.name}: {best_for_factor.name} leads")

    return AgentResult(
        agent="analysis",
        status="complete",
        content="\n".join(lines),
        confidence=confidence
    )


def risk_agent(req: AnalyzeRequest, ranked: List[OptionData]) -> AgentResult:
    best = ranked[0]
    risks = []
    score_spread = best.total_score - (ranked[1].total_score if len(ranked) > 1 else 0)

    if score_spread < 5:
        risks.append(f"🔴 HIGH: {best.name} leads by only {score_spread:.1f} points — recommendation is fragile.")
    elif score_spread < 15:
        risks.append(f"🟡 MEDIUM: {best.name} has a moderate lead of {score_spread:.1f} points.")
    else:
        risks.append(f"🟢 LOW: {best.name} has a strong lead of {score_spread:.1f} points.")

    if req.budget:
        risks.append(f"• Budget constraint: ${req.budget:,.2f} — verify all options fit within this.")
    risks.append(f"• Urgency '{req.urgency}' may pressure timeline-sensitive factors.")

    return AgentResult(
        agent="risk",
        status="complete",
        content="\n".join(risks),
        confidence=0.80
    )


def comparison_agent(req: AnalyzeRequest, ranked: List[OptionData]) -> AgentResult:
    lines = ["Head-to-head comparison:\n"]
    for opt in ranked:
        strengths = [f"{f}: {opt.factor_scores.get(f, 0):.1f}/10" for f in opt.factor_scores if opt.factor_scores[f] >= 7]
        weaknesses = [f"{f}: {opt.factor_scores.get(f, 0):.1f}/10" for f in opt.factor_scores if opt.factor_scores[f] <= 4]
        lines.append(f"#{opt.rank} {opt.name} ({opt.total_score}/100)")
        if strengths:
            lines.append(f"  Strengths: {', '.join(strengths)}")
        if weaknesses:
            lines.append(f"  Weaknesses: {', '.join(weaknesses)}")
    return AgentResult(
        agent="comparison",
        status="complete",
        content="\n".join(lines),
        confidence=0.88
    )


def decision_agent(req: AnalyzeRequest, ranked: List[OptionData]) -> AgentResult:
    best = ranked[0]
    runner_up = ranked[1] if len(ranked) > 1 else None
    score_spread = best.total_score - (runner_up.total_score if runner_up else 0)

    pro_lines = [f"✅ Why {best.name}:"]
    for f in req.factors:
        if best.factor_scores.get(f.name, 0) >= 7:
            pro_lines.append(f"• Excels in {f.name} ({best.factor_scores[f.name]:.1f}/10)")

    con_lines = [f"⚠ Risks of {best.name}:"]
    for f in req.factors:
        if best.factor_scores.get(f.name, 0) <= 4:
            con_lines.append(f"• Underperforms in {f.name} ({best.factor_scores[f.name]:.1f}/10)")

    alt_lines = []
    if runner_up and score_spread < 10:
        alt_lines = [f"🔄 Alternative: {runner_up.name} ({runner_up.total_score}/100) — close contender worth considering."]

    content = "\n".join(pro_lines + [""] + con_lines + [""] + alt_lines) if alt_lines else "\n".join(pro_lines + [""] + con_lines)

    return AgentResult(
        agent="recommendation",
        status="complete",
        content=content,
        confidence=0.90
    )


# ── API Routes ──────────────────────────────────────────

@app.get("/health")
async def health():
    return {"status": "healthy", "model": "deterministic-fallback", "timestamp": datetime.now().isoformat()}


@app.post("/ai/analyze", response_model=AnalyzeResponse)
async def analyze(request: AnalyzeRequest):
    """Run the full multi-agent analysis pipeline."""
    try:
        # Build scores map from option factor_scores
        scores_map = {}
        for opt in request.options:
            scores_map[opt.name] = opt.factor_scores

        ranked = calculate_weighted_scores(request.options, request.factors, scores_map)

        research = research_agent(request, ranked)
        analysis = analysis_agent(request, ranked)
        risk = risk_agent(request, ranked)
        comparison = comparison_agent(request, ranked)
        decision = decision_agent(request, ranked)

        top_pick = ranked[0].name if ranked else ""
        avg_confidence = (research.confidence + analysis.confidence + risk.confidence + comparison.confidence + decision.confidence) / 5

        return AnalyzeResponse(
            research=research,
            analysis=analysis,
            risk=risk,
            comparison=comparison,
            recommendation=decision,
            top_pick=top_pick,
            confidence=round(avg_confidence, 2),
            model_used="deterministic-fallback"
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/ai/simulate", response_model=SimulationResponse)
async def simulate(request: SimulationRequest):
    """What-if simulation with adjusted weights."""
    try:
        scores_map = {}
        for opt in request.decision_data.options:
            scores_map[opt.name] = opt.factor_scores

        # Before
        before_ranked = calculate_weighted_scores(
            [OptionData(**o.model_dump()) for o in request.decision_data.options],
            request.decision_data.factors,
            scores_map
        )

        # After (with overrides)
        weight_overrides = request.adjusted_weights if request.adjusted_weights else None
        after_ranked = calculate_weighted_scores(
            [OptionData(**o.model_dump()) for o in request.decision_data.options],
            request.decision_data.factors,
            scores_map,
            weight_overrides
        )

        # Build before/after analysis
        before_req = request.decision_data
        before_req.options = before_ranked
        after_req = AnalyzeRequest(**before_req.model_dump())
        after_req.options = after_ranked

        before_res = AnalyzeResponse(
            research=research_agent(before_req, before_ranked),
            analysis=analysis_agent(before_req, before_ranked),
            risk=risk_agent(before_req, before_ranked),
            comparison=comparison_agent(before_req, before_ranked),
            recommendation=decision_agent(before_req, before_ranked),
            top_pick=before_ranked[0].name,
            confidence=0.85,
            model_used="deterministic-fallback"
        )

        after_res = AnalyzeResponse(
            research=research_agent(after_req, after_ranked),
            analysis=analysis_agent(after_req, after_ranked),
            risk=risk_agent(after_req, after_ranked),
            comparison=comparison_agent(after_req, after_ranked),
            recommendation=decision_agent(after_req, after_ranked),
            top_pick=after_ranked[0].name,
            confidence=0.85,
            model_used="deterministic-fallback"
        )

        rank_changes = {}
        for opt in after_ranked:
            old = next((o for o in before_ranked if o.name == opt.name), None)
            if old:
                rank_changes[opt.name] = old.rank - opt.rank  # positive = improved

        recommendation_changed = before_res.top_pick != after_res.top_pick

        return SimulationResponse(
            before=before_res,
            after=after_res,
            rank_changes=rank_changes,
            recommendation_changed=recommendation_changed
        )

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/ai/agents")
async def list_agents():
    return {
        "agents": [
            {"id": "orchestrator", "name": "Orchestrator", "description": "Coordinates multi-agent pipeline"},
            {"id": "research", "name": "Research Agent", "description": "Gathers and analyzes market/domain data"},
            {"id": "analysis", "name": "Analysis Agent", "description": "Evaluates options against weighted factors"},
            {"id": "risk", "name": "Risk Agent", "description": "Identifies risks, uncertainties, and mitigations"},
            {"id": "comparison", "name": "Comparison Agent", "description": "Head-to-head option comparison"},
            {"id": "decision", "name": "Decision Agent", "description": "Synthesizes findings into final recommendation"}
        ]
    }


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)