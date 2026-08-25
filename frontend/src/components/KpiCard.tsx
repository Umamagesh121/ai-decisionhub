import { motion } from 'framer-motion';
import { TrendingUp, TrendingDown, Minus } from 'lucide-react';
import { useCountUp } from '../hooks/useCountUp';

interface KpiCardProps {
  icon: React.ReactNode;
  label: string;
  value: number;
  suffix?: string;
  trend?: 'up' | 'down' | 'neutral';
  trendLabel?: string;
  index?: number;
}

export default function KpiCard({ icon, label, value, suffix = '', trend, trendLabel, index = 0 }: KpiCardProps) {
  const count = useCountUp(value, 2000);

  const TrendIcon = trend === 'up' ? TrendingUp : trend === 'down' ? TrendingDown : Minus;
  const trendColor = trend === 'up' ? 'text-green-500' : trend === 'down' ? 'text-red-500' : 'text-gray-400';

  return (
    <motion.div
      initial={{ opacity: 0, y: 20, scale: 0.95 }}
      animate={{ opacity: 1, y: 0, scale: 1 }}
      transition={{ delay: index * 0.08, type: 'spring', stiffness: 260, damping: 20 }}
      className="card card-hover p-5"
    >
      <div className="flex items-start justify-between">
        <div className="flex-1 min-w-0">
          <p className="text-sm font-medium text-gray-500 dark:text-gray-400 truncate">{label}</p>
          <div className="flex items-baseline gap-1 mt-1">
            <span className="text-2xl font-bold text-gray-900 dark:text-white tabular-nums">
              {count.toLocaleString()}
            </span>
            {suffix && (
              <span className="text-sm text-gray-500 dark:text-gray-400">{suffix}</span>
            )}
          </div>
        </div>
        <div className="flex-shrink-0 w-10 h-10 rounded-lg bg-brand-50 dark:bg-brand-900/20 flex items-center justify-center">
          <div className="text-brand-600 dark:text-brand-400">{icon}</div>
        </div>
      </div>
      {(trend || trendLabel) && (
        <div className="flex items-center gap-1 mt-3">
          {trend && <TrendIcon className={`w-4 h-4 ${trendColor}`} />}
          {trendLabel && (
            <span className={`text-xs font-medium ${trendColor}`}>{trendLabel}</span>
          )}
        </div>
      )}
    </motion.div>
  );
}