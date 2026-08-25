package com.decisionhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalDecisions;
    private long activeDecisions;
    private long completedDecisions;
    private double avgConfidence;
    private double successRate;
    private List<DecisionSummary> recentDecisions;
    private Map<String, Long> categoryDistribution;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DecisionSummary {
        private Long id;
        private String title;
        private String status;
        private String category;
        private Double confidenceScore;
        private String recommendedOption;
    }
}