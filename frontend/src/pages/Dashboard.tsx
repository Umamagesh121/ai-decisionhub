import { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import {
  FileText,
  CheckCircle,
  Clock,
  TrendingUp,
  Target,
  PlusCircle,
  Brain,
  ArrowRight,
} from 'lucide-react';
import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from 'recharts';
import KpiCard from '../components/KpiCard';
import type { DashboardData } from '../types';

const COLORS = ['#6366f1', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#06b6d4', '#ec4899'];

const mockDashboard: DashboardData = {
  totalDecisions: 47,
  activeDecisions: 12,
  completedDecisions: 32,
  averageConfidence: 87,
  successRate: 78,
  recentDecisions: [
    { id: 1, title: 'Cloud Migration Strategy', category: 'Technology', urgency: 'High', status: 'completed', confidence: 92, createdAt: '2026-08-20', updatedAt: '2026-08-24', description: '', budget: 0, deadline: '', options: [], factors: [], weights: [], scores: [], comparison: [], outcome: null, outcomeNotes: null, finalChoice: null },
    { id: 2, title: 'Marketing Budget Allocation', category: 'Finance', urgency: 'Medium', status: 'analysis', confidence: 0, createdAt: '2026-08-22', updatedAt: '2026-08-25', description: '', budget: 0, deadline: '', options: [], factors: [], weights: [], scores: [], comparison: [], outcome: null, outcomeNotes: null, finalChoice: null },
    { id: 3, title: 'Vendor Selection Q4', category: 'Operations', urgency: 'High', status: 'active', confidence: 0, createdAt: '2026-08-18', updatedAt: '2026-08-23', description: '', budget: 0, deadline: '', options: [], factors: [], weights: [], scores: [], comparison: [], outcome: null, outcomeNotes: null, finalChoice: null },
    { id: 4, title: 'Product Launch Timeline', category: 'Product', urgency: 'Critical', status: 'draft', confidence: 0, createdAt: '2026-08-25', updatedAt: '2026-08-25', description: '', budget: 0, deadline: '', options: [], factors: [], weights: [], scores: [], comparison: [], outcome: null, outcomeNotes: null, finalChoice: null },
  ],
  categoryDistribution: [
    { category: 'Technology', count: 15 },
    { category: 'Finance', count: 10 },
    { category: 'Operations', count: 8 },
    { category: 'Product', count: 7 },
    { category: 'HR', count: 5 },
    { category: 'Marketing', count: 2 },
  ],
  decisionTrend: [
    { date: 'Aug 19', count: 3 },
    { date: 'Aug 20', count: 5 },
    { date: 'Aug 21', count: 2 },
    { date: 'Aug 22', count: 7 },
    { date: 'Aug 23', count: 4 },
    { date: 'Aug 24', count: 6 },
    { date: 'Aug 25', count: 8 },
  ],
};

const statusBadge: Record<string, string> = {
  draft: 'badge badge-info',
  active: 'badge badge-brand',
  analysis: 'badge badge-warning',
  completed: 'badge badge-success',
  results: 'badge badge-brand',
};

export default function Dashboard() {
  const [data] = useState<DashboardData>(mockDashboard);
  const navigate = useNavigate();

  return (
    <motion.div
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.3 }}
      className="p-4 sm:p-6 lg:p-8 max-w-7xl mx-auto space-y-6"
    >
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Dashboard</h1>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">AI-powered decision intelligence at a glance</p>
        </div>
        <motion.button
          whileHover={{ scale: 1.02 }}
          whileTap={{ scale: 0.98 }}
          onClick={() => navigate('/decisions/new')}
          className="btn-primary gap-2 self-start sm:self-auto"
        >
          <PlusCircle className="w-4 h-4" />
          New Decision
        </motion.button>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
        <KpiCard icon={<FileText className="w-5 h-5" />} label="Total Decisions" value={data.totalDecisions} trend="up" trendLabel="+12% this month" index={0} />
        <KpiCard icon={<Clock className="w-5 h-5" />} label="Active" value={data.activeDecisions} trend="up" trendLabel="4 in progress" index={1} />
        <KpiCard icon={<CheckCircle className="w-5 h-5" />} label="Completed" value={data.completedDecisions} trend="up" trendLabel="78% success rate" index={2} />
        <KpiCard icon={<Target className="w-5 h-5" />} label="Avg Confidence" value={data.averageConfidence} suffix="%" index={3} />
        <KpiCard icon={<TrendingUp className="w-5 h-5" />} label="Success Rate" value={data.successRate} suffix="%" trend="up" trendLabel="+5% vs last month" index={4} />
      </div>

      {/* Charts row */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Category Distribution */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3 }}
          className="card p-5"
        >
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            Decision Categories
          </h2>
          <ResponsiveContainer width="100%" height={280}>
            <PieChart>
              <Pie
                data={data.categoryDistribution}
                cx="50%"
                cy="50%"
                innerRadius={60}
                outerRadius={100}
                dataKey="count"
                nameKey="category"
                animationDuration={1200}
                animationEasing="ease-out"
                label={({ category, percent }) => `${category} ${(percent * 100).toFixed(0)}%`}
                labelLine={{ stroke: '#9ca3af' }}
              >
                {data.categoryDistribution.map((_, i) => (
                  <Cell key={i} fill={COLORS[i % COLORS.length]} />
                ))}
              </Pie>
              <Tooltip
                contentStyle={{
                  backgroundColor: 'rgba(255,255,255,0.95)',
                  border: '1px solid #e5e7eb',
                  borderRadius: '8px',
                  fontSize: '13px',
                }}
              />
            </PieChart>
          </ResponsiveContainer>
        </motion.div>

        {/* Decision Trend */}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          className="card p-5"
        >
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            Decision Activity
          </h2>
          <ResponsiveContainer width="100%" height={280}>
            <LineChart data={data.decisionTrend}>
              <CartesianGrid strokeDasharray="3 3" stroke="#374151" opacity={0.2} />
              <XAxis dataKey="date" tick={{ fontSize: 12, fill: '#6b7280' }} />
              <YAxis tick={{ fontSize: 12, fill: '#6b7280' }} allowDecimals={false} />
              <Tooltip
                contentStyle={{
                  backgroundColor: 'rgba(255,255,255,0.95)',
                  border: '1px solid #e5e7eb',
                  borderRadius: '8px',
                  fontSize: '13px',
                }}
              />
              <Line
                type="monotone"
                dataKey="count"
                name="Decisions"
                stroke="#6366f1"
                strokeWidth={2.5}
                dot={{ fill: '#6366f1', r: 4 }}
                activeDot={{ r: 6, fill: '#4f46e5' }}
                animationDuration={1500}
                animationEasing="ease-out"
              />
            </LineChart>
          </ResponsiveContainer>
        </motion.div>
      </div>

      {/* Recent Decisions */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.5 }}
        className="space-y-4"
      >
        <div className="flex items-center justify-between">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white">Recent Decisions</h2>
          <button onClick={() => navigate('/decisions/new')} className="text-sm text-brand-600 dark:text-brand-400 hover:underline flex items-center gap-1">
            View all <ArrowRight className="w-3 h-3" />
          </button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-4">
          {data.recentDecisions.map((decision, i) => (
            <motion.div
              key={decision.id}
              initial={{ opacity: 0, y: 20, scale: 0.95 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              transition={{ delay: 0.1 * i + 0.5, type: 'spring', stiffness: 260, damping: 20 }}
              onClick={() => navigate(`/decisions/${decision.id}`)}
              className="card card-hover p-4 cursor-pointer"
            >
              <div className="flex items-start justify-between mb-2">
                <h3 className="font-semibold text-gray-900 dark:text-white text-sm line-clamp-2">
                  {decision.title}
                </h3>
                <span className={statusBadge[decision.status] + ' flex-shrink-0 ml-2'}>
                  {decision.status}
                </span>
              </div>
              <div className="flex items-center justify-between text-xs text-gray-500 dark:text-gray-400 mt-3">
                <span className="badge badge-brand">{decision.category}</span>
                <span>{new Date(decision.createdAt).toLocaleDateString()}</span>
              </div>
              {decision.confidence > 0 && (
                <div className="mt-3 flex items-center gap-2">
                  <div className="flex-1 h-1.5 bg-gray-100 dark:bg-gray-800 rounded-full overflow-hidden">
                    <motion.div
                      initial={{ width: 0 }}
                      animate={{ width: `${decision.confidence}%` }}
                      transition={{ duration: 1, delay: 0.5 + i * 0.1 }}
                      className="h-full bg-green-500 rounded-full"
                    />
                  </div>
                  <span className="text-xs font-mono text-gray-500">{decision.confidence}%</span>
                </div>
              )}
            </motion.div>
          ))}
        </div>
      </motion.div>
    </motion.div>
  );
}