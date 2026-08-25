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
public class ComparisonResponse {
    private Long decisionId;
    private String decisionTitle;
    private List<OptionScoreDetail> options;
    private Double confidenceScore;
    private String recommendedOption;
    private Long recommendedOptionId;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OptionScoreDetail {
        private Long optionId;
        private String optionName;
        private Double weightedScore;
        private Integer rank;
        private Map<String, Double> factorScores;
    }
}