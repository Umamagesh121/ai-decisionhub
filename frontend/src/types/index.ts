export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  createdAt: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface DecisionOption {
  id: string;
  name: string;
  description: string;
}

export interface DecisionFactor {
  id: string;
  name: string;
  description: string;
}

export interface FactorWeight {
  factorId: string;
  factorName: string;
  weight: number;
}

export interface OptionScore {
  optionId: string;
  optionName: string;
  scores: { factorId: string; factorName: string; score: number }[];
}

export interface ComparisonResult {
  optionId: string;
  optionName: string;
  totalScore: number;
  rank: number;
  confidence: number;
  explanation: {
    why: string;
    tradeOff: string;
    risk: string;
    alternative: string;
  };
}

export interface SimulationResult {
  originalRank: number;
  newRank: number;
  originalScore: number;
  newScore: number;
  change: 'up' | 'down' | 'same';
}

export interface AgentStatus {
  id: string;
  name: string;
  status: 'idle' | 'active' | 'complete';
  description: string;
}

export interface Decision {
  id: number;
  title: string;
  description: string;
  category: string;
  urgency: string;
  budget: number;
  deadline: string;
  status: 'draft' | 'active' | 'analysis' | 'results' | 'completed';
  options: DecisionOption[];
  factors: DecisionFactor[];
  weights: FactorWeight[];
  scores: OptionScore[];
  comparison: ComparisonResult[];
  outcome: string | null;
  outcomeNotes: string | null;
  finalChoice: string | null;
  confidence: number;
  createdAt: string;
  updatedAt: string;
}

export interface DashboardData {
  totalDecisions: number;
  activeDecisions: number;
  completedDecisions: number;
  averageConfidence: number;
  successRate: number;
  recentDecisions: Decision[];
  categoryDistribution: { category: string; count: number }[];
  decisionTrend: { date: string; count: number }[];
}

export interface AnalyticsData {
  insights: { id: number; text: string; impact: string }[];
  confidenceDistribution: { range: string; count: number }[];
  successRateByCategory: { category: string; rate: number }[];
  decisionTimeline: { month: string; created: number; completed: number }[];
}

export interface WhatIfParams {
  budget: number;
  deadline: string;
  riskTolerance: number;
  factorWeights: FactorWeight[];
}