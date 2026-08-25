import { useState } from 'react';
import { motion } from 'framer-motion';
import { Trophy, ChevronUp, ChevronDown } from 'lucide-react';
import type { ComparisonResult } from '../types';

interface ComparisonTableProps {
  results: ComparisonResult[];
}

export default function ComparisonTable({ results }: ComparisonTableProps) {
  const [sortKey, setSortKey] = useState<'rank' | 'score' | 'confidence'>('rank');

  const sorted = [...results].sort((a, b) => {
    if (sortKey === 'rank') return a.rank - b.rank;
    if (sortKey === 'score') return b.totalScore - a.totalScore;
    return b.confidence - a.confidence;
  });

  const maxScore = Math.max(...results.map((r) => r.totalScore), 1);

  return (
    <div className="overflow-x-auto -mx-4 sm:mx-0">
      <table className="w-full min-w-[600px]">
        <thead>
          <tr className="border-b border-gray-200 dark:border-gray-700">
            <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase">Rank</th>
            <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase">Option</th>
            <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase cursor-pointer select-none" onClick={() => setSortKey('score')}>
              <span className="inline-flex items-center gap-1">
                Score {sortKey === 'score' && <ChevronDown className="w-3 h-3" />}
              </span>
            </th>
            <th className="text-left py-3 px-4 text-xs font-semibold text-gray-500 dark:text-gray-400 uppercase cursor-pointer select-none" onClick={() => setSortKey('confidence')}>
              <span className="inline-flex items-center gap-1">
                Confidence {sortKey === 'confidence' && <ChevronDown className="w-3 h-3" />}
              </span>
            </th>
          </tr>
        </thead>
        <tbody>
          {sorted.map((result, i) => (
            <motion.tr
              key={result.optionId}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: i * 0.1 }}
              className={`border-b border-gray-100 dark:border-gray-800 ${
                result.rank === 1 ? 'bg-green-50/50 dark:bg-green-900/10' : ''
              }`}
            >
              <td className="py-3 px-4">
                <div className="flex items-center gap-2">
                  {result.rank === 1 ? (
                    <Trophy className="w-5 h-5 text-yellow-500" />
                  ) : (
                    <span className="w-6 h-6 rounded-full bg-gray-100 dark:bg-gray-800 flex items-center justify-center text-xs font-bold text-gray-500 dark:text-gray-400">
                      {result.rank}
                    </span>
                  )}
                  {i > 0 && sorted[i - 1].rank > result.rank && (
                    <ChevronUp className="w-4 h-4 text-green-500" />
                  )}
                  {i > 0 && sorted[i - 1].rank < result.rank && (
                    <ChevronDown className="w-4 h-4 text-red-500" />
                  )}
                </div>
              </td>
              <td className="py-3 px-4">
                <span className={`font-medium ${result.rank === 1 ? 'text-green-700 dark:text-green-400' : 'text-gray-900 dark:text-gray-100'}`}>
                  {result.optionName}
                </span>
              </td>
              <td className="py-3 px-4">
                <div className="flex items-center gap-3">
                  <div className="flex-1 h-2 bg-gray-100 dark:bg-gray-800 rounded-full overflow-hidden">
                    <motion.div
                      initial={{ width: 0 }}
                      animate={{ width: `${(result.totalScore / maxScore) * 100}%` }}
                      transition={{ duration: 1, delay: i * 0.1, ease: 'easeOut' }}
                      className={`h-full rounded-full ${
                        result.rank === 1 ? 'bg-green-500' : 'bg-brand-500'
                      }`}
                    />
                  </div>
                  <span className="text-sm font-mono font-bold text-gray-700 dark:text-gray-300 w-12 text-right">
                    {result.totalScore.toFixed(1)}
                  </span>
                </div>
              </td>
              <td className="py-3 px-4">
                <div className="flex items-center gap-2">
                  <div className="flex-1 h-2 bg-gray-100 dark:bg-gray-800 rounded-full overflow-hidden max-w-[80px]">
                    <motion.div
                      initial={{ width: 0 }}
                      animate={{ width: `${result.confidence}%` }}
                      transition={{ duration: 0.8, delay: i * 0.1 + 0.3, ease: 'easeOut' }}
                      className="h-full rounded-full bg-blue-500"
                    />
                  </div>
                  <span className="text-xs font-mono text-gray-500 dark:text-gray-400">
                    {result.confidence}%
                  </span>
                </div>
              </td>
            </motion.tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}