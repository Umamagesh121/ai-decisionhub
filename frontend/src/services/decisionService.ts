import api from './api';
import type {
  Decision,
  DecisionOption,
  DecisionFactor,
  FactorWeight,
  OptionScore,
  ComparisonResult,
  WhatIfParams,
  SimulationResult,
} from '../types';

export const decisionService = {
  async getAll(): Promise<Decision[]> {
    const { data } = await api.get<Decision[]>('/decisions');
    return data;
  },

  async getById(id: number): Promise<Decision> {
    const { data } = await api.get<Decision>(`/decisions/${id}`);
    return data;
  },

  async create(decision: Partial<Decision>): Promise<Decision> {
    const { data } = await api.post<Decision>('/decisions', decision);
    return data;
  },

  async update(id: number, decision: Partial<Decision>): Promise<Decision> {
    const { data } = await api.put<Decision>(`/decisions/${id}`, decision);
    return data;
  },

  async delete(id: number): Promise<void> {
    await api.delete(`/decisions/${id}`);
  },

  async addOption(decisionId: number, option: Omit<DecisionOption, 'id'>): Promise<DecisionOption> {
    const { data } = await api.post<DecisionOption>(`/decisions/${decisionId}/options`, option);
    return data;
  },

  async removeOption(decisionId: number, optionId: string): Promise<void> {
    await api.delete(`/decisions/${decisionId}/options/${optionId}`);
  },

  async addFactor(decisionId: number, factor: Omit<DecisionFactor, 'id'>): Promise<DecisionFactor> {
    const { data } = await api.post<DecisionFactor>(`/decisions/${decisionId}/factors`, factor);
    return data;
  },

  async removeFactor(decisionId: number, factorId: string): Promise<void> {
    await api.delete(`/decisions/${decisionId}/factors/${factorId}`);
  },

  async setWeights(decisionId: number, weights: FactorWeight[]): Promise<void> {
    await api.put(`/decisions/${decisionId}/weights`, { weights });
  },

  async setScores(decisionId: number, scores: OptionScore[]): Promise<void> {
    await api.put(`/decisions/${decisionId}/scores`, { scores });
  },

  async runComparison(decisionId: number): Promise<ComparisonResult[]> {
    const { data } = await api.post<ComparisonResult[]>(`/decisions/${decisionId}/compare`);
    return data;
  },

  async runSimulation(decisionId: number, params: WhatIfParams): Promise<SimulationResult[]> {
    const { data } = await api.post<SimulationResult[]>(`/decisions/${decisionId}/simulate`, params);
    return data;
  },

  async runAnalysis(decisionId: number): Promise<ComparisonResult[]> {
    const { data } = await api.post<ComparisonResult[]>(`/decisions/${decisionId}/analyze`);
    return data;
  },

  async recordOutcome(decisionId: number, outcome: string, notes: string): Promise<Decision> {
    const { data } = await api.put<Decision>(`/decisions/${decisionId}/outcome`, { outcome, notes });
    return data;
  },
};