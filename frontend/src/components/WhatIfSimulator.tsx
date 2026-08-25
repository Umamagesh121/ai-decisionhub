import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Sliders, RotateCcw } from 'lucide-react';
import type { FactorWeight, SimulationResult } from '../types';

interface WhatIfSimulatorProps {
  factors: FactorWeight[];
  onSimulate: (adjustedWeights: FactorWeight[], budget: number, riskTolerance: number) => void;
  results: SimulationResult[] | null;
  loading?: boolean;
}

export default function WhatIfSimulator({ factors, onSimulate, results, loading }: WhatIfSimulatorProps) {
  const [open, setOpen] = useState(false);
  const [budget, setBudget] = useState(100000);
  const [riskTolerance, setRiskTolerance] = useState(5);

  const totalWeight = factors.reduce((s, f) => s + f.weight, 0);

  const handleSimulate = () => {
    onSimulate(factors, budget, riskTolerance);
  };

  return (
    <div className="card">
      <button
        onClick={() => setOpen(!open)}
        className="w-full flex items-center justify-between p-4 text-left"
      >
        <div className="flex items-center gap-2">
          <Sliders className="w-5 h-5 text-brand-600 dark:text-brand-400" />
          <span className="font-semibold text-gray-900 dark:text-gray-100">What-If Simulator</span>
        </div>
        <motion.div animate={{ rotate: open ? 180 : 0 }} transition={{ duration: 0.2 }}>
          <svg className="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
          </svg>
        </motion.div>
      </button>

      <AnimatePresence>
        {open && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: 'auto', opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.3 }}
            className="overflow-hidden"
          >
            <div className="px-4 pb-4 space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Budget (${budget.toLocaleString()})
                </label>
                <input
                  type="range"
                  min={1000}
                  max={1000000}
                  step={1000}
                  value={budget}
                  onChange={(e) => setBudget(Number(e.target.value))}
                  className="w-full accent-brand-600"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Risk Tolerance ({riskTolerance}/10)
                </label>
                <input
                  type="range"
                  min={1}
                  max={10}
                  step={1}
                  value={riskTolerance}
                  onChange={(e) => setRiskTolerance(Number(e.target.value))}
                  className="w-full accent-brand-600"
                />
              </div>

              <div className="space-y-2">
                <p className="text-sm font-medium text-gray-700 dark:text-gray-300">
                  Factor Weights (Total: {totalWeight.toFixed(0)}%)
                </p>
                {factors.map((f) => (
                  <div key={f.factorId} className="flex items-center gap-3 text-sm">
                    <span className="w-24 text-gray-600 dark:text-gray-400 truncate">{f.factorName}</span>
                    <div className="flex-1 h-1.5 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                      <div
                        className="h-full bg-brand-500 rounded-full"
                        style={{ width: `${f.weight}%` }}
                      />
                    </div>
                    <span className="w-10 text-right text-gray-500 dark:text-gray-400 font-mono text-xs">
                      {f.weight.toFixed(0)}%
                    </span>
                  </div>
                ))}
              </div>

              <button
                onClick={handleSimulate}
                disabled={loading}
                className="btn-primary w-full gap-2"
              >
                <RotateCcw className={`w-4 h-4 ${loading ? 'animate-spin' : ''}`} />
                {loading ? 'Simulating...' : 'Run Simulation'}
              </button>

              {results && (
                <motion.div
                  initial={{ opacity: 0, y: 10 }}
                  animate={{ opacity: 1, y: 0 }}
                  className="space-y-2 pt-2 border-t border-gray-200 dark:border-gray-700"
                >
                  <p className="text-sm font-semibold text-gray-900 dark:text-gray-100">Results</p>
                  {results.map((r) => (
                    <div key={r.originalRank.toString()} className="flex items-center justify-between text-sm">
                      <span className="text-gray-600 dark:text-gray-400">Rank #{r.originalRank} → #{r.newRank}</span>
                      <span className={`font-mono font-bold ${
                        r.change === 'up' ? 'text-green-500' :
                        r.change === 'down' ? 'text-red-500' : 'text-gray-500'
                      }`}>
                        {r.originalScore.toFixed(1)} → {r.newScore.toFixed(1)}
                      </span>
                    </div>
                  ))}
                </motion.div>
              )}
            </div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}