import { useState } from 'react';
import { motion } from 'framer-motion';
import {
  BarChart3,
  TrendingUp,
  Lightbulb,
  Target,
  PieChart as PieChartIcon,
} from 'lucide-react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  Legend,
} from 'recharts';
import type { AnalyticsData } from '../types';

const COLORS = ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4'];

const mockAnalytics: AnalyticsData = {
  insights: [
    { id: 1, text: 'Decisions succeed more when scalability weight exceeds 20%', impact: 'high' },
    { id: 2, text: 'Multi-factor decisions have 23% higher confidence scores', impact: 'medium' },
    { id: 3, text: 'Decisions with 5+ options show lower AI confidence', impact: 'medium' },
    { id: 4, text: 'Cost-weighted decisions in Finance category have 92% success rate', impact: 'high' },
    { id: 5, text: 'Urgent decisions benefit from Risk factor at 30%+ weight', impact: 'low' },
  ],
  confidenceDistribution: [
    { range: '0-20%', count: 2 },
    { range: '21-40%', count: 5 },
    { range: '41-60%', count: 8 },
    { range: '61-80%', count: 15 },
    { range: '81-100%', count: 17 },
  ],
  successRateByCategory: [
    { category: 'Technology', rate: 85 },
    { category: 'Finance', rate: 92 },
    { category: 'Operations', rate: 78 },
    { category: 'Product', rate: 81 },
    { category: 'HR', rate: 88 },
    { category: 'Marketing', rate: 73 },
  ],
  decisionTimeline: [
    { month: 'Jan', created: 8, completed: 6 },
    { month: 'Feb', created: 12, completed: 10 },
    { month: 'Mar', created: 15, completed: 13 },
    { month: 'Apr', created: 10, completed: 11 },
    { month: 'May', created: 18, completed: 15 },
    { month: 'Jun', created: 14, completed: 16 },
    { month: 'Jul', created: 20, completed: 18 },
    { month: 'Aug', created: 22, completed: 19 },
  ],
};

export default function Analytics() {
  const [data] = useState<AnalyticsData>(mockAnalytics);

  const impactColors: Record<string, string> = {
    high: 'bg-red-100 dark:bg-red-900/30 text-red-700 dark:text-red-400 border-red-200 dark:border-red-800',
    medium: 'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-700 dark:text-yellow-400 border-yellow-200 dark:border-yellow-800',
    low: 'bg-blue-100 dark:bg-blue-900/30 text-blue-700 dark:text-blue-400 border-blue-200 dark:border-blue-800',
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6"
    >
      <div className="flex items-center gap-3">
        <div className="w-10 h-10 bg-brand-100 dark:bg-brand-900/30 rounded-xl flex items-center justify-center">
          <BarChart3 className="w-5 h-5 text-brand-600 dark:text-brand-400" />
        </div>
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Analytics</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400">AI-generated insights from your decision history</p>
        </div>
      </div>

      {/* Insights */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.1 }}
        className="card p-5 space-y-3"
      >
        <h2 className="text-lg font-semibold text-gray-900 dark:text-white flex items-center gap-2">
          <Lightbulb className="w-5 h-5 text-yellow-500" /> Key Insights
        </h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
          {data.insights.map((insight, i) => (
            <motion.div
              key={insight.id}
              initial={{ opacity: 0, scale: 0.9 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: i * 0.1 }}
              className={`p-3 rounded-lg border text-sm ${impactColors[insight.impact]}`}
            >
              <span className="text-xs font-semibold uppercase tracking-wide">{insight.impact} impact</span>
              <p className="mt-1">{insight.text}</p>
            </motion.div>
          ))}
        </div>
      </motion.div>

      {/* Charts row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Confidence Distribution */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.2 }}
          className="card p-5"
        >
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
            <Target className="w-5 h-5 text-brand-600" /> Confidence Distribution
          </h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={data.confidenceDistribution}>
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" opacity={0.2} />
              <XAxis dataKey="range" tick={{ fontSize: 12, fill: '#6b7280' }} />
              <YAxis tick={{ fontSize: 12, fill: '#6b7280' }} allowDecimals={false} />
              <Tooltip
                contentStyle={{
                  backgroundColor: 'rgba(255,255,255,0.95)',
                  border: '1px solid #e5e7eb',
                  borderRadius: '8px',
                  fontSize: '13px',
                }}
              />
              <Bar dataKey="count" name="Decisions" radius={[6, 6, 0, 0]} animationDuration={1200}>
                {data.confidenceDistribution.map((_, i) => (
                  <Cell key={i} fill={COLORS[Math.min(i, COLORS.length - 1)]} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </motion.div>

        {/* Success Rate by Category */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="card p-5"
        >
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
            <PieChartIcon className="w-5 h-5 text-brand-600" /> Success Rate by Category
          </h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={data.successRateByCategory} layout="vertical">
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" opacity={0.2} />
              <XAxis type="number" domain={[0, 100]} tick={{ fontSize: 12, fill: '#6b7280' }} />
              <YAxis dataKey="category" type="category" tick={{ fontSize: 12, fill: '#6b7280' }} width={80} />
              <Tooltip
                formatter={(value: number) => [`${value}%`, 'Success Rate']}
                contentStyle={{
                  backgroundColor: 'rgba(255,255,255,0.95)',
                  border: '1px solid #e5e7eb',
                  borderRadius: '8px',
                  fontSize: '13px',
                }}
              />
              <Bar dataKey="rate" name="Success Rate" radius={[0, 6, 6, 0]} animationDuration={1200} fill="#6366f1" />
            </BarChart>
          </ResponsiveContainer>
        </motion.div>
      </div>

      {/* Decision Timeline */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.4 }}
        className="card p-5"
      >
        <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4 flex items-center gap-2">
          <TrendingUp className="w-5 h-5 text-brand-600" /> Decision Timeline
        </h2>
        <ResponsiveContainer width="100%" height={320}>
          <LineChart data={data.decisionTimeline}>
            <CartesianGrid strokeDasharray="3 3" stroke="#374151" opacity={0.2} />
            <XAxis dataKey="month" tick={{ fontSize: 12, fill: '#6b7280' }} />
            <YAxis tick={{ fontSize: 12, fill: '#6b7280' }} allowDecimals={false} />
            <Tooltip
              contentStyle={{
                backgroundColor: 'rgba(255,255,255,0.95)',
                border: '1px solid #e5e7eb',
                borderRadius: '8px',
                fontSize: '13px',
              }}
            />
            <Legend />
            <Line
              type="monotone"
              dataKey="created"
              name="Created"
              stroke="#6366f1"
              strokeWidth={2.5}
              dot={{ fill: '#6366f1', r: 4 }}
              activeDot={{ r: 6 }}
              animationDuration={1500}
            />
            <Line
              type="monotone"
              dataKey="completed"
              name="Completed"
              stroke="#10b981"
              strokeWidth={2.5}
              dot={{ fill: '#10b981', r: 4 }}
              activeDot={{ r: 6 }}
              animationDuration={1500}
              animationBegin={300}
            />
          </LineChart>
        </ResponsiveContainer>
      </motion.div>
    </motion.div>
  );
}