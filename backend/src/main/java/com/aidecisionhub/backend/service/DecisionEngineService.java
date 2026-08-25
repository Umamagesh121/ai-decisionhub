package com.aidecisionhub.backend.service;

import com.aidecisionhub.backend.entity.McpToolEntity;
import com.aidecisionhub.backend.model.TaskType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DecisionEngineService {

    @Value("${app.decision.weights.quality:0.4}")
    private double qualityWeight;

    @Value("${app.decision.weights.speed:0.25}")
    private double speedWeight;

    @Value("${app.decision.weights.cost:0.2}")
    private double costWeight;

    @Value("${app.decision.weights.risk:0.15}")
    private double riskWeight;

    @Value("${app.decision.risk-threshold:0.65}")
    private double riskThreshold;

    public DecisionSelection evaluate(TaskType taskType, List<McpToolEntity> candidates) {
        if (candidates.isEmpty()) {
            throw new IllegalStateException("No active tools available for task type: " + taskType);
        }

        double maxCost = candidates.stream()
            .map(McpToolEntity::getAvgCost)
            .mapToDouble(BigDecimal::doubleValue)
            .max().orElse(1.0);

        double maxLatency = candidates.stream()
            .map(McpToolEntity::getAvgLatencyMs)
            .mapToDouble(Integer::doubleValue)
            .max().orElse(1.0);

        List<CandidateScore> scored = new ArrayList<>();
        for (McpToolEntity candidate : candidates) {
            double qualityScore = clamp(candidate.getReliabilityScore().doubleValue());
            double costScore = clamp(1.0 - (candidate.getAvgCost().doubleValue() / Math.max(0.0001, maxCost)));
            double speedScore = clamp(1.0 - (candidate.getAvgLatencyMs() / Math.max(1.0, maxLatency)));

            double sensitivity = switch (taskType) {
                case CODE_EXEC -> 0.35;
                case GENERATION -> 0.20;
                case SUMMARIZATION -> 0.12;
                case RETRIEVAL -> 0.15;
                case CLASSIFICATION -> 0.10;
                case VALIDATION -> 0.08;
            };
            double riskScore = clamp((1.0 - qualityScore) + sensitivity);

            double finalScore = (qualityWeight * qualityScore)
                + (speedWeight * speedScore)
                + (costWeight * costScore)
                - (riskWeight * riskScore);

            scored.add(new CandidateScore(
                candidate.getName(),
                bd(costScore),
                bd(qualityScore),
                bd(speedScore),
                bd(riskScore),
                bd(finalScore)
            ));
        }

        scored.sort(Comparator.comparing(CandidateScore::finalScore).reversed());
        CandidateScore winner = scored.getFirst();
        boolean requiresApproval = winner.riskScore().doubleValue() >= riskThreshold;

        return new DecisionSelection(winner, scored, requiresApproval);
    }

    private static BigDecimal bd(double value) {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP);
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    public record CandidateScore(
        String tool,
        BigDecimal costScore,
        BigDecimal qualityScore,
        BigDecimal speedScore,
        BigDecimal riskScore,
        BigDecimal finalScore
    ) {}

    public record DecisionSelection(
        CandidateScore winner,
        List<CandidateScore> rankedCandidates,
        boolean requiresApproval
    ) {}
}
