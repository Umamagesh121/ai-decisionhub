import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useParams, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Brain,
  CheckCircle,
  Clock,
  Target,
  ChevronDown,
  Save,
} from 'lucide-react';
import ComparisonTable from '../components/ComparisonTable';
import RadarChart from '../components/RadarChart';
import WhatIfSimulator from '../components/WhatIfSimulator';
import ScoreCounter from '../components/ScoreCounter';
import type { Decision, ComparisonResult, FactorWeight, SimulationResult } from '../types';

const mockDecision: Decision = {
  id: 1,
  title: 'Cloud Migration Strategy',
  description: 'Determine the best approach for migrating our on-premise infrastructure to the cloud, considering cost, timeline, and operational impact.',
  category: 'Technology',
  urgency: 'High',
  budget: 250000,
  deadline: '2026-12-31',
  status: 'completed',
  options: [
    { id: 'opt-1', name: 'AWS Full Migration', description: 'Complete migration to AWS' },
    { id: 'opt-2', name: 'Azure Hybrid', description: 'Hybrid approach with Azure' },
    { id: 'opt-3', name: 'GCP + Multi-cloud', description: 'Multi-cloud strategy with GCP' },
  ],
  factors: [
    { id: 'fct-1', name: 'Cost', description: 'Total cost of ownership' },
    { id: 'fct-2', name: 'Scalability', description: 'Ability to scale' },
    { id: 'fct-3', name: 'Security', description: 'Security posture' },
    { id: 'fct-4', name: 'Timeline', description: 'Migration timeline' },
  ],
  weights: [
    { factorId: 'fct-1', factorName: 'Cost', weight: 35 },
    { factorId: 'fct-2', factorName: 'Scalability', weight: 25 },
    { factorId: 'fct-3', factorName: 'Security', weight: 25 },
    { factorId: 'fct-4', factorName: 'Timeline', weight: 15 },
  ],
  scores: [
    {
      optionId: 'opt-1', optionName: 'AWS Full Migration',
      scores: [
        { factorId: 'fct-1', factorName: 'Cost', score: 7 },
        { factorId: 'fct-2', factorName: 'Scalability', score: 9 },
        { factorId: 'fct-3', factorName: 'Security', score: 8 },
        { factorId: 'fct-4', factorName: 'Timeline', score: 6 },
      ],
    },
    {
      optionId: 'opt-2', optionName: 'Azure Hybrid',
      scores: [
        { factorId: 'fct-1', factorName: 'Cost', score: 8 },
        { factorId: 'fct-2', factorName: 'Scalability', score: 7 },
        { factorId: 'fct-3', factorName: 'Security', score: 9 },
        { factorId: 'fct-4', factorName: 'Timeline', score: 8 },
      ],
    },
    {
      optionId: 'opt-3', optionName: 'GCP + Multi-cloud',
      scores: [
        { factorId: 'fct-1', factorName: 'Cost', score: 6 },
        { factorId: 'fct-2', factorName: 'Scalability', score: 9 },
        { factorId: 'fct-3', factorName: 'Security', score: 7 },
        { factorId: 'fct-4', factorName: 'Timeline', score: 5 },
      ],
    },
  ],
  comparison: [
    { optionId: 'opt-2', optionName: 'Azure Hybrid', totalScore: 8.1, rank: 1, confidence: 91, explanation: { why: 'Azure Hybrid offers the best balance of cost, security, and timeline.', tradeOff: 'Slightly less scalability than full AWS.', risk: 'Vendor lock-in with Azure ecosystem.', alternative: 'Consider a multi-cloud approach for resilience.' } },
    { optionId: 'opt-1', optionName: 'AWS Full Migration', totalScore: 7.65, rank: 2, confidence: 83, explanation: { why: 'AWS has the most mature ecosystem.', tradeOff: 'Higher costs than Azure.', risk: 'Migration complexity.', alternative: 'Phased migration approach.' } },
    { optionId: 'opt-3', optionName: 'GCP + Multi-cloud', totalScore: 6.85, rank: 3, confidence: 72, explanation: { why: 'Best scalability and flexibility.', tradeOff: 'High complexity and cost.', risk: 'Multi-cloud management overhead.', alternative: 'Start with one provider, expand later.' } },
  ],
  outcome: null,
  outcomeNotes: null,
  finalChoice: null,
  confidence: 91,
  createdAt: '2026-08-20',
  updatedAt: '2026-08-24',
};

export default function DecisionDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [decision] = useState<Decision>(mockDecision);
  const [activeSection, setActiveSection] = useState<'overview' | 'comparison' | 'analysis' | 'outcome'>('overview');
  const [outcomeNotes, setOutcomeNotes] = useState('');
  const [outcomeResult, setOutcomeResult] = useState('success');
  const [simResults, setSimResults] = useState<SimulationResult[] | null>(null);
  const [simLoading, setSimLoading] = useState(false);

  const comparison = decision.comparison || [];

  const handleSimulate = async (_weights: FactorWeight[], _budget: number, _risk: number) => {
    setSimLoading(true);
    await new Promise((r) => setTimeout(r, 1200));
    setSimResults([
      { originalRank: 1, newRank: 2, originalScore: 8.1, newScore: 7.8, change: 'down' },
      { originalRank: 2, newRank: 1, originalScore: 7.65, newScore: 8.2, change: 'up' },
      { originalRank: 3, newRank: 3, originalScore: 6.85, newScore: 7.0, change: 'same' },
    ]);
    setSimLoading(false);
  };

  const tabs = [
    { key: 'overview', label: 'Overview' },
    { key: 'comparison', label: 'Comparison' },
    { key: 'analysis', label: 'AI Analysis' },
    { key: 'outcome', label: 'Outcome' },
  ] as const;

  const statusBadge: Record<string, string> = {
    draft: 'badge badge-info',
    active: 'badge badge-brand',
    analysis: 'badge badge-warning',
    completed: 'badge badge-success',
    results: 'badge badge-brand',
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className="p-4 sm:p-6 lg:p-8 max-w-6xl mx-auto space-y-6"
    >
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div className="flex items-center gap-3">
          <button onClick={() => navigate('/dashboard')} className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg">
            <ArrowLeft className="w-5 h-5 text-gray-500" />
          </button>
          <div>
            <h1 className="text-xl sm:text-2xl font-bold text-gray-900 dark:text-white">{decision.title}</h1>
            <div className="flex items-center gap-3 mt-1">
              <span className={statusBadge[decision.status]}>{decision.status}</span>
              <span className="text-xs text-gray-500 dark:text-gray-400">{decision.category}</span>
              <span className="text-xs text-gray-500 dark:text-gray-400">{decision.urgency}</span>
            </div>
          </div>
        </div>
      </div>

      {/* KPIs */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.1 }} className="card p-3 text-center">
          <p className="text-xs text-gray-500 dark:text-gray-400">Confidence</p>
          <ScoreCounter value={decision.confidence} suffix="%" className="text-xl text-brand-600 dark:text-brand-400" />
        </motion.div>
        <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.15 }} className="card p-3 text-center">
          <p className="text-xs text-gray-500 dark:text-gray-400">Budget</p>
          <p className="text-xl font-bold text-gray-900 dark:text-white">${(decision.budget / 1000).toFixed(0)}K</p>
        </motion.div>
        <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.2 }} className="card p-3 text-center">
          <p className="text-xs text-gray-500 dark:text-gray-400">Options</p>
          <p className="text-xl font-bold text-gray-900 dark:text-white">{decision.options.length}</p>
        </motion.div>
        <motion.div initial={{ opacity: 0, y: 10 }} animate={{ opacity: 1, y: 0 }} transition={{ delay: 0.25 }} className="card p-3 text-center">
          <p className="text-xs text-gray-500 dark:text-gray-400">Factors</p>
          <p className="text-xl font-bold text-gray-900 dark:text-white">{decision.factors.length}</p>
        </motion.div>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 bg-gray-100 dark:bg-gray-800 p-1 rounded-lg overflow-x-auto">
        {tabs.map((tab) => (
          <button
            key={tab.key}
            onClick={() => setActiveSection(tab.key)}
            className={`px-4 py-2 text-sm font-medium rounded-md whitespace-nowrap transition-colors ${
              activeSection === tab.key
                ? 'bg-white dark:bg-gray-900 text-brand-600 dark:text-brand-400 shadow-sm'
                : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      <AnimatePresence mode="wait">
        <motion.div
          key={activeSection}
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -10 }}
          transition={{ duration: 0.2 }}
        >
          {/* Overview */}
          {activeSection === 'overview' && (
            <div className="space-y-6">
              <div className="card p-5">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-3">Description</h2>
                <p className="text-gray-600 dark:text-gray-400">{decision.description}</p>
              </div>

              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div className="card p-5">
                  <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-3">Options</h2>
                  <div className="space-y-3">
                    {decision.options.map((opt, i) => (
                      <motion.div key={opt.id} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.1 }} className="p-3 bg-gray-50 dark:bg-gray-800/50 rounded-lg">
                        <div className="flex items-center gap-2">
                          <span className="w-6 h-6 rounded-full bg-brand-100 dark:bg-brand-900/30 text-brand-700 dark:text-brand-400 flex items-center justify-center text-xs font-bold flex-shrink-0">
                            {i + 1}
                          </span>
                          <span className="font-medium text-gray-900 dark:text-gray-100">{opt.name}</span>
                        </div>
                        <p className="text-sm text-gray-500 dark:text-gray-400 mt-1 ml-8">{opt.description}</p>
                      </motion.div>
                    ))}
                  </div>
                </div>

                <div className="card p-5">
                  <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-3">Factors</h2>
                  <div className="space-y-3">
                    {decision.factors.map((fct, i) => (
                      <motion.div key={fct.id} initial={{ opacity: 0, x: -10 }} animate={{ opacity: 1, x: 0 }} transition={{ delay: i * 0.1 }} className="p-3 bg-gray-50 dark:bg-gray-800/50 rounded-lg">
                        <div className="flex items-center justify-between">
                          <span className="font-medium text-gray-900 dark:text-gray-100">{fct.name}</span>
                          {decision.weights[i] && (
                            <span className="text-xs font-mono text-brand-600 dark:text-brand-400">{decision.weights[i].weight}%</span>
                          )}
                        </div>
                        <p className="text-sm text-gray-500 dark:text-gray-400 mt-0.5">{fct.description}</p>
                      </motion.div>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )}

          {/* Comparison */}
          {activeSection === 'comparison' && (
            <div className="space-y-6">
              <div className="card p-5">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Comparison Table</h2>
                <ComparisonTable results={comparison} />
              </div>

              {decision.factors.length >= 3 && (
                <div className="card p-5">
                  <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">Factor Radar</h2>
                  <RadarChart
                    factors={decision.factors.map((f) => f.name)}
                    data={comparison.map((r) => {
                      const scoreData = decision.scores.find((s) => s.optionId === r.optionId);
                      return {
                        name: r.optionName,
                        values: decision.factors.map((f) => scoreData?.scores.find((sc) => sc.factorId === f.id)?.score ?? 0),
                        color: r.rank === 1 ? '#10b981' : r.rank === 2 ? '#6366f1' : '#f59e0b',
                      };
                    })}
                  />
                </div>
              )}

              <WhatIfSimulator
                factors={decision.weights}
                onSimulate={handleSimulate}
                results={simResults}
                loading={simLoading}
              />
            </div>
          )}

          {/* AI Analysis */}
          {activeSection === 'analysis' && (
            <div className="space-y-4">
              <h2 className="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
                <Brain className="w-5 h-5 text-brand-600" /> AI Analysis
              </h2>
              {comparison.map((r, i) => (
                <motion.div
                  key={r.optionId}
                  initial={{ opacity: 0, y: 20 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: i * 0.15 }}
                  className="card p-4"
                >
                  <div className="flex items-center justify-between mb-3">
                    <h3 className="font-semibold text-gray-900 dark:text-white">{r.optionName}</h3>
                    <span className="text-xs font-mono text-gray-500">Score: {r.totalScore}</span>
                  </div>
                  <div className="space-y-2">
                    <details className="group">
                      <summary className="flex items-center gap-1 text-sm font-medium text-green-600 dark:text-green-400 cursor-pointer list-none">
                        <ChevronDown className="w-3 h-3 transition-transform group-open:rotate-180" />
                        Why this option?
                      </summary>
                      <p className="text-sm text-gray-600 dark:text-gray-400 mt-1 ml-4">{r.explanation.why}</p>
                    </details>
                    <details className="group">
                      <summary className="flex items-center gap-1 text-sm font-medium text-yellow-600 dark:text-yellow-400 cursor-pointer list-none">
                        <ChevronDown className="w-3 h-3 transition-transform group-open:rotate-180" />
                        Trade-off
                      </summary>
                      <p className="text-sm text-gray-600 dark:text-gray-400 mt-1 ml-4">{r.explanation.tradeOff}</p>
                    </details>
                    <details className="group">
                      <summary className="flex items-center gap-1 text-sm font-medium text-red-600 dark:text-red-400 cursor-pointer list-none">
                        <ChevronDown className="w-3 h-3 transition-transform group-open:rotate-180" />
                        Risk
                      </summary>
                      <p className="text-sm text-gray-600 dark:text-gray-400 mt-1 ml-4">{r.explanation.risk}</p>
                    </details>
                    <details className="group">
                      <summary className="flex items-center gap-1 text-sm font-medium text-blue-600 dark:text-blue-400 cursor-pointer list-none">
                        <ChevronDown className="w-3 h-3 transition-transform group-open:rotate-180" />
                        Alternative
                      </summary>
                      <p className="text-sm text-gray-600 dark:text-gray-400 mt-1 ml-4">{r.explanation.alternative}</p>
                    </details>
                  </div>
                </motion.div>
              ))}
            </div>
          )}

          {/* Outcome */}
          {activeSection === 'outcome' && (
            <div className="space-y-6">
              <div className="card p-5 space-y-4">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Record Outcome</h2>
                <p className="text-sm text-gray-500 dark:text-gray-400">
                  Once the decision is implemented, record the actual outcome to improve future analyses.
                </p>

                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">Result</label>
                  <div className="flex gap-3">
                    {['success', 'partial', 'failure'].map((r) => (
                      <label
                        key={r}
                        className={`flex-1 p-3 rounded-lg border cursor-pointer text-center text-sm font-medium transition-colors ${
                          outcomeResult === r
                            ? r === 'success' ? 'border-green-500 bg-green-50 dark:bg-green-900/20 text-green-700 dark:text-green-400' :
                              r === 'partial' ? 'border-yellow-500 bg-yellow-50 dark:bg-yellow-900/20 text-yellow-700 dark:text-yellow-400' :
                              'border-red-500 bg-red-50 dark:bg-red-900/20 text-red-700 dark:text-red-400'
                            : 'border-gray-200 dark:border-gray-700 text-gray-500'
                        }`}
                      >
                        <input
                          type="radio"
                          name="outcomeResult"
                          value={r}
                          checked={outcomeResult === r}
                          onChange={(e) => setOutcomeResult(e.target.value)}
                          className="sr-only"
                        />
                        {r === 'success' && <CheckCircle className="w-5 h-5 mx-auto mb-1" />}
                        {r === 'partial' && <Clock className="w-5 h-5 mx-auto mb-1" />}
                        {r === 'failure' && <Target className="w-5 h-5 mx-auto mb-1" />}
                        {r.charAt(0).toUpperCase() + r.slice(1)}
                      </label>
                    ))}
                  </div>
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">Notes</label>
                  <textarea
                    value={outcomeNotes}
                    onChange={(e) => setOutcomeNotes(e.target.value)}
                    rows={4}
                    className="input-field resize-none"
                    placeholder="What went well? What could be improved?"
                  />
                </div>

                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  disabled={!outcomeNotes.trim()}
                  className="btn-primary w-full gap-2"
                >
                  <Save className="w-4 h-4" /> Save Outcome
                </motion.button>
              </div>
            </div>
          )}
        </motion.div>
      </AnimatePresence>
    </motion.div>
  );
}