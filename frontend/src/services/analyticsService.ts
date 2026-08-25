import api from './api';
import type { DashboardData, AnalyticsData } from '../types';

export const analyticsService = {
  async getDashboard(): Promise<DashboardData> {
    const { data } = await api.get<DashboardData>('/analytics/dashboard');
    return data;
  },

  async getInsights(): Promise<AnalyticsData['insights']> {
    const { data } = await api.get<{ insights: AnalyticsData['insights'] }>('/analytics/insights');
    return data.insights;
  },

  async getTrends(): Promise<AnalyticsData> {
    const { data } = await api.get<AnalyticsData>('/analytics/trends');
    return data;
  },
};