import { useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  ArrowRight,
  Check,
  Save,
  Plus,
  Trash2,
  Brain,
  Sparkles,
} from 'lucide-react';
import StepIndicator from '../components/StepIndicator';
import AgentNetwork from '../components/AgentNetwork';
import ComparisonTable from '../components/ComparisonTable';
import RadarChart from '../components/RadarChart';
import ScoreCounter from '../components/ScoreCounter';
import type {
  DecisionOption,
  DecisionFactor,
  FactorWeight,
  OptionScore,
  ComparisonResult,
  AgentStatus,
} from '../types';

const STEPS = [
  'Problem', 'Options', 'Factors', 'Weights',
  'Scores', 'Analysis', 'Results', 'Final',
];

export default function DecisionWorkspace() {
  const navigate = useNavigate();
  const [step, setStep] = useState(0);
  const [loading, setLoading] = useState(false);

  // Step 1
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('Technology');
  const [urgency, setUrgency] = useState('Medium');
  const [budget, setBudget] = useState(50000);
  const [deadline, setDeadline] = useState('');

  // Step 2
  const [options, setOptions] = useState<DecisionOption[]>([
    { id: 'opt-1', name: 'Option A', description: 'First option' },
    { id: 'opt-2', name: 'Option B', description: 'Second option' },
  ]);

  // Step 3
  const [factors, setFactors] = useState<DecisionFactor[]>([
    { id: 'fct-1', name: 'Cost', description: 'Financial impact' },
    { id: 'fct-2', name: 'Risk', description: 'Risk assessment' },
    { id: 'fct-3', name: 'Scalability', description: 'Future growth potential' },
  ]);

  // Step 4
  const [weights, setWeights] = useState<FactorWeight[]>([]);

  // Step 5
  const [scores, setScores] = useState<OptionScore[]>([]);

  // Step 6
  const [agentStatuses, setAgentStatuses] = useState<AgentStatus[]>([
    { id: 'research', name: 'Research', status: 'idle', description: 'Data gathering' },
    { id: 'analysis', name: 'Analysis', status: 'idle', description: 'Deep analysis' },
    { id: 'risk', name: 'Risk', status: 'idle', description: 'Risk evaluation' },
    { id: 'comparison', name: 'Compare', status: 'idle', description: 'Side-by-side' },
    { id: 'decision', name: 'Decision', status: 'idle', description: 'Final recommendation' },
  ]);
  const [orchestratorStatus, setOrchestratorStatus] = useState<'idle' | 'active' | 'complete'>('idle');

  // Step 7
  const [results, setResults] = useState<ComparisonResult[]>([]);

  // Step 8
  const [actionPlan, setActionPlan] = useState('');
  const [finalChoice, setFinalChoice] = useState('');

  const addOption = () => {
    const id = `opt-${Date.now()}`;
    setOptions([...options, { id, name: `Option ${options.length + 1}`, description: '' }]);
  };

  const removeOption = (id: string) => {
    if (options.length <= 2) return;
    setOptions(options.filter((o) => o.id !== id));
  };

  const updateOption = (id: string, field: 'name' | 'description', value: string) => {
    setOptions(options.map((o) => (o.id === id ? { ...o, [field]: value } : o)));
  };

  const addFactor = () => {
    const id = `fct-${Date.now()}`;
    setFactors([...factors, { id, name: `Factor ${factors.length + 1}`, description: '' }]);
  };

  const removeFactor = (id: string) => {
    if (factors.length <= 2) return;
    setFactors(factors.filter((f) => f.id !== id));
  };

  const updateFactor = (id: string, field: 'name' | 'description', value: string) => {
    setFactors(factors.map((f) => (f.id === id ? { ...f, [field]: value } : f)));
  };

  const initializeWeights = () => {
    const equal = Math.floor(100 / factors.length);
    const remainder = 100 - equal * factors.length;
    const w: FactorWeight[] = factors.map((f, i) => ({
      factorId: f.id,
      factorName: f.name,
      weight: equal + (i === 0 ? remainder : 0),
    }));
    setWeights(w);
  };

  const updateWeight = (factorId: string, newWeight: number) => {
    setWeights(weights.map((w) => (w.factorId === factorId ? { ...w, weight: newWeight } : w)));
  };

  const initializeScores = () => {
    const s: OptionScore[] = options.map((opt) => ({
      optionId: opt.id,
      optionName: opt.name,
      scores: factors.map((fct) => ({
        factorId: fct.id,
        factorName: fct.name,
        score: 5,
      })),
    }));
    setScores(s);
  };

  const updateScore = (optionId: string, factorId: string, newScore: number) => {
    setScores(
      scores.map((os) =>
        os.optionId === optionId
          ? {
              ...os,
              scores: os.scores.map((s) =>
                s.factorId === factorId ? { ...s, score: newScore } : s,
              ),
            }
          : os,
      ),
    );
  };

  const runAnalysis = async () => {
    setAgentStatuses((prev) => prev.map((a) => ({ ...a, status: 'idle' as const })));
    setOrchestratorStatus('active');

    const agents = ['research', 'analysis', 'risk', 'comparison', 'decision'];
    for (let i = 0; i < agents.length; i++) {
      await new Promise((r) => setTimeout(r, 1200));
      setAgentStatuses((prev) =>
        prev.map((a) => (a.id === agents[i] ? { ...a, status: 'complete' as const } : a)),
      );
    }
    setOrchestratorStatus('complete');

    // Build mock results
    const wMap: Record<string, number> = {};
    weights.forEach((w) => (wMap[w.factorId] = w.weight / 100));

    const resultsData: ComparisonResult[] = options.map((opt, i) => {
      const scoreData = scores.find((s) => s.optionId === opt.id);
      let totalScore = 0;
      if (scoreData) {
        scoreData.scores.forEach((sc) => {
          totalScore += sc.score * (wMap[sc.factorId] || 0.33);
        });
      }
      return {
        optionId: opt.id,
        optionName: opt.name,
        totalScore: Math.round(totalScore * 100) / 100,
        rank: i + 1,
        confidence: 85 - i * 8 + Math.floor(Math.random() * 10),
        explanation: {
          why: `${opt.name} scores well on weighted factors.`,
          tradeOff: 'May have higher initial costs.',
          risk: 'Implementation timeline risk.',
          alternative: `Could consider a hybrid approach.`,
        },
      };
    });

    resultsData.sort((a, b) => b.totalScore - a.totalScore);
    resultsData.forEach((r, i) => (r.rank = i + 1));

    setResults(resultsData);
  };

  const goForward = () => {
    if (step === 3 && weights.length === 0) initializeWeights();
    if (step === 4 && scores.length === 0) initializeScores();
    if (step === 5) {
      setLoading(true);
      runAnalysis().finally(() => setLoading(false));
    }
    setStep((s) => Math.min(s + 1, STEPS.length - 1));
  };

  const goBack = () => setStep((s) => Math.max(s - 1, 0));

  const totalWeight = weights.reduce((s, w) => s + w.weight, 0);

  const canProceed = () => {
    if (step === 0) return title.trim().length > 0 && description.trim().length > 0;
    if (step === 1) return options.length >= 2;
    if (step === 2) return factors.length >= 2;
    if (step === 3) return Math.abs(totalWeight - 100) < 1;
    if (step === 5) return true;
    if (step === 6) return results.length > 0;
    if (step === 7) return finalChoice && actionPlan.trim().length > 0;
    return true;
  };

  const slideVariants = {
    enter: (dir: number) => ({ x: dir > 0 ? 300 : -300, opacity: 0 }),
    center: { x: 0, opacity: 1 },
    exit: (dir: number) => ({ x: dir < 0 ? 300 : -300, opacity: 0 }),
  };

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="p-4 sm:p-6 lg:p-8 max-w-5xl mx-auto space-y-6"
    >
      <div className="flex items-center gap-3 mb-2">
        <button onClick={() => navigate('/dashboard')} className="p-1.5 hover:bg-gray-100 dark:hover:bg-gray-800 rounded-lg">
          <ArrowLeft className="w-5 h-5 text-gray-500" />
        </button>
        <h1 className="text-xl sm:text-2xl font-bold text-gray-900 dark:text-white">New Decision</h1>
      </div>

      {/* Step indicator */}
      <StepIndicator steps={STEPS} currentStep={step} onStepClick={(s: number) => s < step && setStep(s)} />

      {/* Dynamic step content */}
      <div className="card p-5 sm:p-6 min-h-[420px]">
        <AnimatePresence mode="wait" custom={1}>
          <motion.div
            key={step}
            custom={1}
            variants={slideVariants}
            initial="enter"
            animate="center"
            exit="exit"
            transition={{ duration: 0.3, ease: 'easeInOut' }}
          >
            {/* Step 0: Problem */}
            {step === 0 && (
              <div className="space-y-5">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
                  <Brain className="w-5 h-5 text-brand-600" /> Define Your Problem
                </h2>
                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">Decision Title</label>
                  <input type="text" value={title} onChange={(e) => setTitle(e.target.value)} className="input-field" placeholder="e.g., Choose a cloud provider for migration" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">Description</label>
                  <textarea value={description} onChange={(e) => setDescription(e.target.value)} rows={4} className="input-field resize-none" placeholder="Describe the decision context, goals, and constraints..." />
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">Category</label>
                    <select value={category} onChange={(e) => setCategory(e.target.value)} className="input-field">
                      {['Technology', 'Finance', 'Operations', 'Product', 'HR', 'Marketing', 'Strategy'].map((c) => (
                        <option key={c} value={c}>{c}</option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">Urgency</label>
                    <select value={urgency} onChange={(e) => setUrgency(e.target.value)} className="input-field">
                      {['Low', 'Medium', 'High', 'Critical'].map((u) => (
                        <option key={u} value={u}>{u}</option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">Budget ($)</label>
                    <input type="number" value={budget} onChange={(e) => setBudget(Number(e.target.value))} className="input-field" />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">Deadline</label>
                    <input type="date" value={deadline} onChange={(e) => setDeadline(e.target.value)} className="input-field" />
                  </div>
                </div>
              </div>
            )}

            {/* Step 1: Options */}
            {step === 1 && (
              <div className="space-y-5">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Add Decision Options</h2>
                <p className="text-sm text-gray-500 dark:text-gray-400">Define the alternatives you're choosing between (min 2)</p>
                <div className="space-y-3">
                  {options.map((opt, i) => (
                    <motion.div
                      key={opt.id}
                      initial={{ opacity: 0, scale: 0.9 }}
                      animate={{ opacity: 1, scale: 1 }}
                      transition={{ delay: i * 0.1 }}
                      className="flex items-start gap-3 p-3 bg-gray-50 dark:bg-gray-800/50 rounded-lg"
                    >
                      <span className="w-7 h-7 rounded-full bg-brand-100 dark:bg-brand-900/30 text-brand-700 dark:text-brand-400 flex items-center justify-center text-xs font-bold flex-shrink-0 mt-1">
                        {i + 1}
                      </span>
                      <div className="flex-1 space-y-2">
                        <input
                          type="text"
                          value={opt.name}
                          onChange={(e) => updateOption(opt.id, 'name', e.target.value)}
                          className="input-field text-sm"
                          placeholder="Option name"
                        />
                        <input
                          type="text"
                          value={opt.description}
                          onChange={(e) => updateOption(opt.id, 'description', e.target.value)}
                          className="input-field text-sm"
                          placeholder="Brief description"
                        />
                      </div>
                      {options.length > 2 && (
                        <button onClick={() => removeOption(opt.id)} className="p-1.5 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg">
                          <Trash2 className="w-4 h-4" />
                        </button>
                      )}
                    </motion.div>
                  ))}
                </div>
                <motion.button whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }} onClick={addOption} className="btn-secondary w-full gap-2">
                  <Plus className="w-4 h-4" /> Add Option
                </motion.button>
              </div>
            )}

            {/* Step 2: Factors */}
            {step === 2 && (
              <div className="space-y-5">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Define Decision Factors</h2>
                <p className="text-sm text-gray-500 dark:text-gray-400">What criteria will you use to evaluate each option? (min 2)</p>
                <div className="space-y-3">
                  {factors.map((fct, i) => (
                    <motion.div
                      key={fct.id}
                      initial={{ opacity: 0, scale: 0.9 }}
                      animate={{ opacity: 1, scale: 1 }}
                      transition={{ delay: i * 0.1 }}
                      className="flex items-start gap-3 p-3 bg-gray-50 dark:bg-gray-800/50 rounded-lg"
                    >
                      <span className="w-7 h-7 rounded-full bg-purple-100 dark:bg-purple-900/30 text-purple-700 dark:text-purple-400 flex items-center justify-center text-xs font-bold flex-shrink-0 mt-1">
                        {i + 1}
                      </span>
                      <div className="flex-1 space-y-2">
                        <input type="text" value={fct.name} onChange={(e) => updateFactor(fct.id, 'name', e.target.value)} className="input-field text-sm" placeholder="Factor name" />
                        <input type="text" value={fct.description} onChange={(e) => updateFactor(fct.id, 'description', e.target.value)} className="input-field text-sm" placeholder="Brief description" />
                      </div>
                      {factors.length > 2 && (
                        <button onClick={() => removeFactor(fct.id)} className="p-1.5 text-red-500 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-lg">
                          <Trash2 className="w-4 h-4" />
                        </button>
                      )}
                    </motion.div>
                  ))}
                </div>
                <motion.button whileHover={{ scale: 1.02 }} whileTap={{ scale: 0.98 }} onClick={addFactor} className="btn-secondary w-full gap-2">
                  <Plus className="w-4 h-4" /> Add Factor
                </motion.button>
              </div>
            )}

            {/* Step 3: Weights */}
            {step === 3 && (
              <div className="space-y-5">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Set Factor Weights</h2>
                <p className={`text-sm ${Math.abs(totalWeight - 100) < 1 ? 'text-green-600 dark:text-green-400' : 'text-red-500'}`}>
                  Total weight: {totalWeight}% (must sum to 100%)
                </p>
                <div className="space-y-4">
                  {weights.map((w, i) => (
                    <motion.div
                      key={w.factorId}
                      initial={{ opacity: 0, x: -20 }}
                      animate={{ opacity: 1, x: 0 }}
                      transition={{ delay: i * 0.1 }}
                      className="space-y-1.5"
                    >
                      <div className="flex items-center justify-between">
                        <span className="text-sm font-medium text-gray-700 dark:text-gray-300">{w.factorName}</span>
                        <span className="text-sm font-mono font-bold text-brand-600 dark:text-brand-400">{w.weight}%</span>
                      </div>
                      <input
                        type="range"
                        min={0}
                        max={100}
                        value={w.weight}
                        onChange={(e) => updateWeight(w.factorId, Number(e.target.value))}
                        className="w-full accent-brand-600"
                      />
                    </motion.div>
                  ))}
                </div>
              </div>
            )}

            {/* Step 4: Scores */}
            {step === 4 && (
              <div className="space-y-5">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Score Options vs Factors</h2>
                <p className="text-sm text-gray-500 dark:text-gray-400">Rate each option against each factor (0-10)</p>
                <div className="overflow-x-auto -mx-2 sm:mx-0">
                  <table className="w-full min-w-[500px] text-sm">
                    <thead>
                      <tr>
                        <th className="text-left py-2 px-2 font-medium text-gray-500 dark:text-gray-400">Option</th>
                        {factors.map((f) => (
                          <th key={f.id} className="text-center py-2 px-2 font-medium text-gray-500 dark:text-gray-400 text-xs">
                            {f.name}
                          </th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {scores.map((os, i) => (
                        <motion.tr key={os.optionId} initial={{ opacity: 0 }} animate={{ opacity: 1 }} transition={{ delay: i * 0.1 }}>
                          <td className="py-2 px-2 font-medium text-gray-900 dark:text-gray-100">{os.optionName}</td>
                          {os.scores.map((sc) => (
                            <td key={sc.factorId} className="py-2 px-2 text-center">
                              <input
                                type="number"
                                min={0}
                                max={10}
                                value={sc.score}
                                onChange={(e) => updateScore(os.optionId, sc.factorId, Math.min(10, Math.max(0, Number(e.target.value))))}
                                className="w-14 text-center input-field text-sm py-1"
                              />
                            </td>
                          ))}
                        </motion.tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {/* Step 5: Loading / Agent Network */}
            {step === 5 && (
              <div className="space-y-6">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
                  <Sparkles className="w-5 h-5 text-brand-600" /> AI Analysis
                </h2>
                {loading ? (
                  <div className="text-center py-8">
                    <p className="text-sm text-gray-500 dark:text-gray-400 mb-6 animate-pulse">
                      AI agents are analyzing your decision...
                    </p>
                    <AgentNetwork agents={agentStatuses} orchestratorStatus={orchestratorStatus} />
                  </div>
                ) : orchestratorStatus === 'complete' ? (
                  <div className="space-y-4">
                    <div className="flex items-center gap-2 p-3 bg-green-50 dark:bg-green-900/20 rounded-lg">
                      <Check className="w-5 h-5 text-green-600" />
                      <span className="text-sm font-medium text-green-700 dark:text-green-400">Analysis complete!</span>
                    </div>
                    <AgentNetwork agents={agentStatuses} orchestratorStatus={orchestratorStatus} />
                  </div>
                ) : (
                  <div className="text-center py-8">
                    <p className="text-sm text-gray-500 dark:text-gray-400 mb-4">
                      Ready to run the AI-powered analysis
                    </p>
                    <AgentNetwork agents={agentStatuses} orchestratorStatus="idle" />
                  </div>
                )}
              </div>
            )}

            {/* Step 6: Results */}
            {step === 6 && results.length > 0 && (
              <div className="space-y-6">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Results</h2>

                {/* Score counters */}
                <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-5 gap-3">
                  {results.map((r, i) => (
                    <motion.div
                      key={r.optionId}
                      initial={{ opacity: 0, scale: 0.8 }}
                      animate={{ opacity: 1, scale: 1 }}
                      transition={{ delay: i * 0.15 }}
                      className={`card p-3 text-center ${r.rank === 1 ? 'ring-2 ring-green-500' : ''}`}
                    >
                      <p className="text-xs text-gray-500 dark:text-gray-400 truncate">{r.optionName}</p>
                      <ScoreCounter value={r.totalScore} className="text-xl text-brand-600 dark:text-brand-400" />
                      <div className="mt-1 text-xs text-gray-400">{r.confidence}% confidence</div>
                    </motion.div>
                  ))}
                </div>

                {/* Radar chart */}
                {factors.length >= 3 && (
                  <div className="card p-4">
                    <h3 className="text-sm font-semibold text-gray-900 dark:text-white mb-2">Factor Comparison</h3>
                    <RadarChart
                      factors={factors.map((f) => f.name)}
                      data={results.slice(0, 3).map((r) => {
                        const scoreData = scores.find((s) => s.optionId === r.optionId);
                        return {
                          name: r.optionName,
                          values: factors.map((f) => scoreData?.scores.find((sc) => sc.factorId === f.id)?.score ?? 0),
                          color: r.rank === 1 ? '#10b981' : r.rank === 2 ? '#6366f1' : '#f59e0b',
                        };
                      })}
                    />
                  </div>
                )}

                {/* Comparison table */}
                <div>
                  <h3 className="text-sm font-semibold text-gray-900 dark:text-white mb-2">Comparison</h3>
                  <div className="card p-4">
                    <ComparisonTable results={results} />
                  </div>
                </div>

                {/* AI Explanation */}
                {results[0]?.explanation && (
                  <motion.div
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="card p-4 space-y-3"
                  >
                    <h3 className="text-sm font-semibold text-gray-900 dark:text-white flex items-center gap-2">
                      <Brain className="w-4 h-4 text-brand-600" /> AI Explanation
                    </h3>
                    <div className="space-y-2">
                      <div className="p-3 bg-green-50 dark:bg-green-900/10 rounded-lg">
                        <p className="text-xs font-semibold text-green-700 dark:text-green-400">Why {results[0].optionName}?</p>
                        <p className="text-sm text-green-600 dark:text-green-500 mt-0.5">{results[0].explanation.why}</p>
                      </div>
                      <div className="p-3 bg-yellow-50 dark:bg-yellow-900/10 rounded-lg">
                        <p className="text-xs font-semibold text-yellow-700 dark:text-yellow-400">Trade-off</p>
                        <p className="text-sm text-yellow-600 dark:text-yellow-500 mt-0.5">{results[0].explanation.tradeOff}</p>
                      </div>
                      <div className="p-3 bg-red-50 dark:bg-red-900/10 rounded-lg">
                        <p className="text-xs font-semibold text-red-700 dark:text-red-400">Risk</p>
                        <p className="text-sm text-red-600 dark:text-red-500 mt-0.5">{results[0].explanation.risk}</p>
                      </div>
                      <div className="p-3 bg-blue-50 dark:bg-blue-900/10 rounded-lg">
                        <p className="text-xs font-semibold text-blue-700 dark:text-blue-400">Alternative</p>
                        <p className="text-sm text-blue-600 dark:text-blue-500 mt-0.5">{results[0].explanation.alternative}</p>
                      </div>
                    </div>
                  </motion.div>
                )}
              </div>
            )}

            {/* Step 7: Final */}
            {step === 7 && (
              <div className="space-y-5">
                <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Finalize Your Decision</h2>

                <div className="card p-4 space-y-3">
                  <h3 className="text-sm font-semibold text-gray-900 dark:text-white">Choose Final Option</h3>
                  {results.map((r) => (
                    <label
                      key={r.optionId}
                      className={`flex items-center gap-3 p-3 rounded-lg border cursor-pointer transition-colors ${
                        finalChoice === r.optionId
                          ? 'border-brand-500 bg-brand-50 dark:bg-brand-900/20'
                          : 'border-gray-200 dark:border-gray-700 hover:border-gray-300'
                      }`}
                    >
                      <input
                        type="radio"
                        name="finalChoice"
                        value={r.optionId}
                        checked={finalChoice === r.optionId}
                        onChange={(e) => setFinalChoice(e.target.value)}
                        className="accent-brand-600"
                      />
                      <div className="flex-1">
                        <span className="font-medium text-gray-900 dark:text-gray-100">{r.optionName}</span>
                        <span className="ml-2 text-xs text-gray-500">Score: {r.totalScore}</span>
                      </div>
                      {r.rank === 1 && <span className="badge badge-success">Recommended</span>}
                    </label>
                  ))}
                </div>

                <div>
                  <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1.5">Action Plan</label>
                  <textarea
                    value={actionPlan}
                    onChange={(e) => setActionPlan(e.target.value)}
                    rows={5}
                    className="input-field resize-none"
                    placeholder="Outline your action plan, next steps, timeline..."
                  />
                </div>

                <motion.button
                  whileHover={{ scale: 1.02 }}
                  whileTap={{ scale: 0.98 }}
                  disabled={!finalChoice || !actionPlan.trim()}
                  onClick={() => {
                    setLoading(true);
                    setTimeout(() => {
                      setLoading(false);
                      navigate('/dashboard');
                    }, 1500);
                  }}
                  className="btn-primary w-full gap-2"
                >
                  {loading ? (
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  ) : (
                    <Save className="w-4 h-4" />
                  )}
                  {loading ? 'Finalizing...' : 'Finalize Decision'}
                </motion.button>
              </div>
            )}
          </motion.div>
        </AnimatePresence>
      </div>

      {/* Navigation */}
      <div className="flex items-center justify-between">
        <button
          onClick={goBack}
          disabled={step === 0}
          className="btn-secondary gap-2 disabled:opacity-40"
        >
          <ArrowLeft className="w-4 h-4" /> Back
        </button>
        <span className="text-sm text-gray-400 dark:text-gray-500">
          Step {step + 1} of {STEPS.length}
        </span>
        <button
          onClick={goForward}
          disabled={!canProceed() || (step === 5 && loading) || step === STEPS.length - 1}
          className="btn-primary gap-2 disabled:opacity-40"
        >
          {loading && step === 5 ? (
            <>
              <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
              Analyzing...
            </>
          ) : (
            <>
              Next <ArrowRight className="w-4 h-4" />
            </>
          )}
        </button>
      </div>
    </motion.div>
  );
}