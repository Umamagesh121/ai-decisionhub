package com.aidecisionhub.backend.dto;

import java.util.List;

public record DecisionAnalyticsResponse(
    long totalDecisions,
    double verificationPassRate,
    double averagePredictionError,
    double averageLatencyMs,
    double averageCost,
    List<ToolUsage> topTools
) {
    public record ToolUsage(String tool, long count, double averageFinalScore) {}
}
