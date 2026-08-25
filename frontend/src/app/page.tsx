"use client";

import { FormEvent, useMemo, useState } from "react";

type RequestTrace = {
  requestId: string;
  status: string;
  rawInput: string;
  requirementSpec: Record<string, unknown>;
  tasks: Array<{
    id: string;
    parentTaskId?: string;
    taskType: string;
    status: string;
    input: Record<string, unknown>;
    output: Record<string, unknown>;
    createdAt: string;
  }>;
  decisions: Array<{
    id: string;
    taskId: string;
    chosenTool: string;
    candidates: Record<string, unknown>;
    costScore: number;
    qualityScore: number;
    speedScore: number;
    riskScore: number;
    finalScore: number;
    requiresApproval: boolean;
    createdAt: string;
  }>;
  executions: Array<{
    id: string;
    decisionId: string;
    actualCost: number;
    actualLatencyMs: number;
    rawOutput: Record<string, unknown>;
    startedAt: string;
    completedAt: string;
  }>;
  verifications: Array<{
    id: string;
    executionId: string;
    passed: boolean;
    verificationScore: number;
    notes: string;
    createdAt: string;
  }>;
  outcomes: Array<{
    id: string;
    decisionId: string;
    predictedScore: number;
    actualQuality: number;
    predictionError: number;
    learnedWeightAdjustment: Record<string, unknown>;
    createdAt: string;
  }>;
};

type Tool = {
  id: string;
  name: string;
  capabilityTags: string[];
  avgCost: number;
  avgLatencyMs: number;
  reliabilityScore: number;
  active: boolean;
};

type Analytics = {
  totalDecisions: number;
  verificationPassRate: number;
  averagePredictionError: number;
  averageLatencyMs: number;
  averageCost: number;
  topTools: Array<{ tool: string; count: number; averageFinalScore: number }>;
};

const apiBase = process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8080";

async function callApi<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${apiBase}${path}`, {
    headers: {
      "Content-Type": "application/json",
      ...(init?.headers ?? {}),
    },
    ...init,
  });

  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `API error ${response.status}`);
  }

  return response.json() as Promise<T>;
}

export default function Home() {
  const [rawInput, setRawInput] = useState(
    "Build an AI workflow that classifies support tickets, retrieves policy docs, and drafts a response under budget $25"
  );
  const [requestIdInput, setRequestIdInput] = useState("");
  const [trace, setTrace] = useState<RequestTrace | null>(null);
  const [tools, setTools] = useState<Tool[]>([]);
  const [analytics, setAnalytics] = useState<Analytics | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const kpis = useMemo(() => {
    if (!trace) return null;
    return {
      tasks: trace.tasks.length,
      decisions: trace.decisions.length,
      passRate:
        trace.verifications.length === 0
          ? 0
          : Math.round(
              (trace.verifications.filter((v) => v.passed).length /
                trace.verifications.length) *
                100
            ),
      totalCost: trace.executions
        .reduce((sum, e) => sum + (e.actualCost ?? 0), 0)
        .toFixed(4),
    };
  }, [trace]);

  async function handleSubmit(event: FormEvent) {
    event.preventDefault();
    setLoading(true);
    setError(null);
    try {
      const created = await callApi<RequestTrace>("/api/v1/requests", {
        method: "POST",
        body: JSON.stringify({ rawInput }),
      });
      setTrace(created);
      setRequestIdInput(created.requestId);
      await refreshAnalytics();
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }

  async function fetchTrace() {
    if (!requestIdInput.trim()) return;
    setLoading(true);
    setError(null);
    try {
      const result = await callApi<RequestTrace>(
        `/api/v1/requests/${requestIdInput.trim()}`
      );
      setTrace(result);
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }

  async function refreshTools() {
    setLoading(true);
    setError(null);
    try {
      const data = await callApi<Tool[]>("/api/v1/tools");
      setTools(data);
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setLoading(false);
    }
  }

  async function refreshAnalytics() {
    try {
      const data = await callApi<Analytics>("/api/v1/analytics/decisions");
      setAnalytics(data);
    } catch {
      // ignore initial empty analytics errors
    }
  }

  return (
    <main className="mx-auto max-w-7xl px-4 py-8">
      <header className="mb-6 flex flex-wrap items-center justify-between gap-3">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">AI DecisionHub</h1>
          <p className="mt-1 text-sm text-slate-300">
            Predict → Decide → Execute → Verify → Learn → Improve
          </p>
        </div>
        <div className="flex gap-2">
          <span className="badge">Spring Boot + Next.js</span>
          <span className="badge">PostgreSQL-ready + Redis-ready</span>
        </div>
      </header>

      <section className="grid gap-6 lg:grid-cols-2">
        <article className="card p-5">
          <h2 className="mb-3 text-lg font-semibold">Submit New Request</h2>
          <form onSubmit={handleSubmit} className="space-y-3">
            <textarea
              value={rawInput}
              onChange={(e) => setRawInput(e.target.value)}
              className="w-full rounded-xl border border-slate-700 bg-slate-950/60 p-3 text-sm outline-none focus:border-blue-500"
              rows={5}
              placeholder="Describe what DecisionHub should orchestrate..."
              required
            />
            <button
              type="submit"
              disabled={loading}
              className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold transition hover:bg-blue-500 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {loading ? "Running workflow..." : "Run End-to-End Workflow"}
            </button>
          </form>
        </article>

        <article className="card p-5">
          <h2 className="mb-3 text-lg font-semibold">Load Existing Trace</h2>
          <div className="flex gap-2">
            <input
              value={requestIdInput}
              onChange={(e) => setRequestIdInput(e.target.value)}
              className="w-full rounded-xl border border-slate-700 bg-slate-950/60 p-3 text-sm outline-none focus:border-blue-500"
              placeholder="Request UUID"
            />
            <button
              onClick={fetchTrace}
              className="rounded-xl border border-slate-600 px-4 py-2 text-sm font-semibold hover:bg-slate-800"
            >
              Fetch
            </button>
          </div>
          <div className="mt-3 flex flex-wrap gap-2">
            <button
              onClick={refreshTools}
              className="rounded-xl border border-slate-600 px-3 py-1.5 text-xs hover:bg-slate-800"
            >
              Refresh Tools
            </button>
            <button
              onClick={refreshAnalytics}
              className="rounded-xl border border-slate-600 px-3 py-1.5 text-xs hover:bg-slate-800"
            >
              Refresh Analytics
            </button>
          </div>
          {error && (
            <p className="mt-3 rounded-lg border border-red-500/40 bg-red-950/50 px-3 py-2 text-sm text-red-200">
              {error}
            </p>
          )}
        </article>
      </section>

      {kpis && (
        <section className="mt-6 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
          <KpiCard title="Tasks" value={kpis.tasks.toString()} />
          <KpiCard title="Decisions" value={kpis.decisions.toString()} />
          <KpiCard title="Verification Pass" value={`${kpis.passRate}%`} />
          <KpiCard title="Total Cost" value={`$${kpis.totalCost}`} />
        </section>
      )}

      {trace && (
        <section className="mt-6 space-y-5">
          <article className="card p-5">
            <h3 className="mb-2 text-lg font-semibold">Request Summary</h3>
            <div className="grid gap-2 text-sm md:grid-cols-2">
              <div>
                <span className="text-slate-400">Request ID:</span> {trace.requestId}
              </div>
              <div>
                <span className="text-slate-400">Status:</span> {trace.status}
              </div>
              <div className="md:col-span-2">
                <span className="text-slate-400">Input:</span> {trace.rawInput}
              </div>
            </div>
          </article>

          <DataTable title="Tasks" headers={["Type", "Status", "Parent", "Created"]} rows={trace.tasks.map((t) => [t.taskType, t.status, t.parentTaskId ?? "-", new Date(t.createdAt).toLocaleString()])} />

          <DataTable
            title="Decisions"
            headers={["Task", "Tool", "Final", "Risk", "Needs Approval"]}
            rows={trace.decisions.map((d) => [
              d.taskId,
              d.chosenTool,
              Number(d.finalScore).toFixed(4),
              Number(d.riskScore).toFixed(4),
              d.requiresApproval ? "Yes" : "No",
            ])}
          />

          <DataTable
            title="Executions"
            headers={["Decision", "Latency", "Cost", "Status"]}
            rows={trace.executions.map((e) => [
              e.decisionId,
              `${e.actualLatencyMs} ms`,
              `$${Number(e.actualCost).toFixed(4)}`,
              String(e.rawOutput?.status ?? "unknown"),
            ])}
          />

          <DataTable
            title="Verifications"
            headers={["Execution", "Passed", "Score", "Notes"]}
            rows={trace.verifications.map((v) => [
              v.executionId,
              v.passed ? "Yes" : "No",
              Number(v.verificationScore).toFixed(4),
              v.notes,
            ])}
          />
        </section>
      )}

      <section className="mt-6 grid gap-6 lg:grid-cols-2">
        <article className="card p-5">
          <h3 className="mb-3 text-lg font-semibold">Tool Registry</h3>
          {tools.length === 0 ? (
            <p className="text-sm text-slate-400">Click “Refresh Tools” to load MCP tools.</p>
          ) : (
            <div className="space-y-2 text-sm">
              {tools.map((tool) => (
                <div key={tool.id} className="rounded-lg border border-slate-700 p-3">
                  <div className="font-medium">{tool.name}</div>
                  <div className="mt-1 text-xs text-slate-400">
                    tags: {tool.capabilityTags.join(", ")} • reliability: {tool.reliabilityScore} • cost: ${tool.avgCost}
                  </div>
                </div>
              ))}
            </div>
          )}
        </article>

        <article className="card p-5">
          <h3 className="mb-3 text-lg font-semibold">Decision Analytics</h3>
          {!analytics ? (
            <p className="text-sm text-slate-400">Run workflow first or click “Refresh Analytics”.</p>
          ) : (
            <div className="space-y-3 text-sm">
              <div className="grid grid-cols-2 gap-2">
                <Metric label="Total Decisions" value={analytics.totalDecisions.toString()} />
                <Metric
                  label="Verification Pass"
                  value={`${(analytics.verificationPassRate * 100).toFixed(1)}%`}
                />
                <Metric
                  label="Avg Prediction Error"
                  value={analytics.averagePredictionError.toFixed(4)}
                />
                <Metric label="Avg Latency" value={`${analytics.averageLatencyMs.toFixed(0)} ms`} />
              </div>

              <div>
                <h4 className="mb-1 font-medium">Top Tools</h4>
                <div className="space-y-1 text-xs text-slate-300">
                  {analytics.topTools.map((item) => (
                    <div key={item.tool} className="flex justify-between rounded border border-slate-700 px-2 py-1">
                      <span>{item.tool}</span>
                      <span>
                        {item.count} runs • avg score {item.averageFinalScore.toFixed(3)}
                      </span>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}
        </article>
      </section>
    </main>
  );
}

function KpiCard({ title, value }: { title: string; value: string }) {
  return (
    <div className="card p-4">
      <div className="text-xs text-slate-400">{title}</div>
      <div className="mt-1 text-2xl font-semibold">{value}</div>
    </div>
  );
}

function Metric({ label, value }: { label: string; value: string }) {
  return (
    <div className="rounded-lg border border-slate-700 p-2">
      <div className="text-xs text-slate-400">{label}</div>
      <div className="mt-1 font-medium">{value}</div>
    </div>
  );
}

function DataTable({
  title,
  headers,
  rows,
}: {
  title: string;
  headers: string[];
  rows: string[][];
}) {
  return (
    <article className="card overflow-hidden">
      <div className="border-b border-slate-700 px-4 py-3 text-lg font-semibold">{title}</div>
      <div className="overflow-x-auto">
        <table className="min-w-full text-left text-sm">
          <thead>
            <tr className="bg-slate-900/70">
              {headers.map((header) => (
                <th key={header} className="px-4 py-2 font-medium text-slate-300">
                  {header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows.length === 0 ? (
              <tr>
                <td className="px-4 py-3 text-slate-500" colSpan={headers.length}>
                  No data yet.
                </td>
              </tr>
            ) : (
              rows.map((row, idx) => (
                <tr key={`${title}-${idx}`} className="border-t border-slate-800/80">
                  {row.map((cell, cellIdx) => (
                    <td key={`${idx}-${cellIdx}`} className="px-4 py-2 align-top text-slate-200">
                      {cell}
                    </td>
                  ))}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </article>
  );
}
