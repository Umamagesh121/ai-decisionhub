import { motion } from 'framer-motion';
import { CheckCircle, Loader2, Circle } from 'lucide-react';
import type { AgentStatus } from '../types';

interface AgentNetworkProps {
  agents: AgentStatus[];
  orchestratorStatus: 'idle' | 'active' | 'complete';
}

const CENTER_X = 200;
const CENTER_Y = 200;
const RADIUS = 130;
const ANGLES = [-90, -18, 54, 126, 198];

export default function AgentNetwork({ agents, orchestratorStatus }: AgentNetworkProps) {
  const nodePositions = agents.map((_, i) => {
    const angle = (ANGLES[i] * Math.PI) / 180;
    return {
      x: CENTER_X + RADIUS * Math.cos(angle),
      y: CENTER_Y + RADIUS * Math.sin(angle),
    };
  });

  const getNodeColor = (status: string) => {
    switch (status) {
      case 'active': return '#6366f1';
      case 'complete': return '#10b981';
      default: return '#6b7280';
    }
  };

  const getNodeIcon = (status: string) => {
    switch (status) {
      case 'active': return <Loader2 className="w-4 h-4 animate-spin" />;
      case 'complete': return <CheckCircle className="w-4 h-4" />;
      default: return <Circle className="w-4 h-4" />;
    }
  };

  const orchestratorColor = getNodeColor(orchestratorStatus);

  return (
    <svg viewBox="0 0 400 380" className="w-full max-w-md mx-auto">
      {/* Connections */}
      {nodePositions.map((pos, i) => (
        <motion.line
          key={`conn-${i}`}
          x1={CENTER_X}
          y1={CENTER_Y}
          x2={pos.x}
          y2={pos.y}
          stroke={getNodeColor(agents[i]?.status || 'idle')}
          strokeWidth={2}
          strokeDasharray="8 4"
          initial={{ pathLength: 0, opacity: 0.3 }}
          animate={{
            pathLength: 1,
            opacity: agents[i]?.status === 'complete' ? 0.8 : 0.4,
            strokeDashoffset: agents[i]?.status === 'active' ? [0, -24] : 0,
          }}
          transition={{
            pathLength: { duration: 1, delay: i * 0.3 },
            strokeDashoffset: { repeat: Infinity, duration: 1.5, ease: 'linear' },
          }}
        />
      ))}

      {/* Agent nodes */}
      {nodePositions.map((pos, i) => {
        const agent = agents[i];
        const color = getNodeColor(agent?.status || 'idle');
        return (
          <motion.g key={`agent-${i}`} initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ delay: i * 0.2, type: 'spring' }}>
            {agent?.status === 'active' && (
              <motion.circle
                cx={pos.x}
                cy={pos.y}
                r={22}
                fill="none"
                stroke={color}
                strokeWidth={2}
                initial={{ opacity: 0.6, r: 16 }}
                animate={{ opacity: 0, r: 30 }}
                transition={{ repeat: Infinity, duration: 1.5, ease: 'easeOut' }}
              />
            )}
            <circle cx={pos.x} cy={pos.y} r={16} fill={color} className="transition-colors duration-500" />
            <foreignObject x={pos.x - 8} y={pos.y - 8} width={16} height={16}>
              <div className="flex items-center justify-center w-full h-full text-white">
                {getNodeIcon(agent?.status || 'idle')}
              </div>
            </foreignObject>
            <text
              x={pos.x}
              y={pos.y + 32}
              textAnchor="middle"
              className="text-[10px] fill-gray-600 dark:fill-gray-400 font-medium"
            >
              {agent?.name || ''}
            </text>
            <text
              x={pos.x}
              y={pos.y + 44}
              textAnchor="middle"
              className="text-[9px] fill-gray-400 dark:fill-gray-500"
            >
              {agent?.status === 'active' ? 'Working...' : agent?.status === 'complete' ? 'Done' : 'Waiting'}
            </text>
          </motion.g>
        );
      })}

      {/* Central orchestrator */}
      <motion.g initial={{ scale: 0 }} animate={{ scale: 1 }} transition={{ delay: 0.5, type: 'spring' }}>
        {orchestratorStatus === 'active' && (
          <motion.circle
            cx={CENTER_X}
            cy={CENTER_Y}
            r={30}
            fill="none"
            stroke={orchestratorColor}
            strokeWidth={2}
            initial={{ opacity: 0.8, r: 22 }}
            animate={{ opacity: 0, r: 38 }}
            transition={{ repeat: Infinity, duration: 2, ease: 'easeOut' }}
          />
        )}
        <circle cx={CENTER_X} cy={CENTER_Y} r={22} fill={orchestratorColor} className="transition-colors duration-500" />
        <text
          x={CENTER_X}
          y={CENTER_Y + 4}
          textAnchor="middle"
          className="text-[11px] font-bold fill-white"
        >
          AI
        </text>
        <text
          x={CENTER_X}
          y={CENTER_Y + 40}
          textAnchor="middle"
          className="text-[10px] fill-gray-600 dark:fill-gray-400 font-medium"
        >
          Orchestrator
        </text>
      </motion.g>
    </svg>
  );
}