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
public class SimulationResponse {
    private Long decisionId;
    private List<ScoreDetail> beforeScores;
    private List<ScoreDetail> afterScores;
    private List<RankChange> rankChanges;
    private boolean recommendationChanged;
    private String previousRecommendation;
    private String newRecommendation;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreDetail {
        private Long optionId;
        private String optionName;
        private Double score;
        private Integer rank;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankChange {
        private Long optionId;
        private String optionName;
        private Integer oldRank;
        private Integer newRank;
    }
}