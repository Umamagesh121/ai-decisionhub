package com.aidecisionhub.backend.service;

import com.aidecisionhub.backend.dto.DecisionAnalyticsResponse;
import com.aidecisionhub.backend.entity.DecisionEntity;
import com.aidecisionhub.backend.entity.DecisionOutcomeEntity;
import com.aidecisionhub.backend.entity.ExecutionEntity;
import com.aidecisionhub.backend.entity.VerificationEntity;
import com.aidecisionhub.backend.repository.DecisionOutcomeRepository;
import com.aidecisionhub.backend.repository.DecisionRepository;
import com.aidecisionhub.backend.repository.ExecutionRepository;
import com.aidecisionhub.backend.repository.VerificationRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DecisionAnalyticsService {

    private final DecisionRepository decisionRepository;
    private final DecisionOutcomeRepository decisionOutcomeRepository;
    private final VerificationRepository verificationRepository;
    private final ExecutionRepository executionRepository;

    public DecisionAnalyticsService(
        DecisionRepository decisionRepository,
        DecisionOutcomeRepository decisionOutcomeRepository,
        VerificationRepository verificationRepository,
        ExecutionRepository executionRepository
    ) {
        this.decisionRepository = decisionRepository;
        this.decisionOutcomeRepository = decisionOutcomeRepository;
        this.verificationRepository = verificationRepository;
        this.executionRepository = executionRepository;
    }

    public DecisionAnalyticsResponse getAnalytics() {
        List<DecisionEntity> decisions = decisionRepository.findAll();
        List<DecisionOutcomeEntity> outcomes = decisionOutcomeRepository.findAll();
        List<VerificationEntity> verifications = verificationRepository.findAll();
        List<ExecutionEntity> executions = executionRepository.findAll();

        long totalDecisions = decisions.size();

        double passRate = verifications.isEmpty()
            ? 0.0
            : verifications.stream().filter(VerificationEntity::isPassed).count() / (double) verifications.size();

        double avgPredictionError = outcomes.stream()
            .mapToDouble(v -> v.getPredictionError().doubleValue())
            .average().orElse(0.0);

        double avgLatency = executions.stream()
            .mapToInt(v -> v.getActualLatencyMs() == null ? 0 : v.getActualLatencyMs())
            .average().orElse(0.0);

        double avgCost = executions.stream()
            .mapToDouble(v -> v.getActualCost() == null ? 0.0 : v.getActualCost().doubleValue())
            .average().orElse(0.0);

        Map<String, List<DecisionEntity>> groupedByTool = decisions.stream()
            .collect(Collectors.groupingBy(DecisionEntity::getChosenTool));

        List<DecisionAnalyticsResponse.ToolUsage> topTools = groupedByTool.entrySet().stream()
            .map(entry -> {
                double avgFinalScore = entry.getValue().stream()
                    .mapToDouble(v -> v.getFinalScore().doubleValue())
                    .average().orElse(0.0);
                return new DecisionAnalyticsResponse.ToolUsage(entry.getKey(), entry.getValue().size(), avgFinalScore);
            })
            .sorted(Comparator.comparingLong(DecisionAnalyticsResponse.ToolUsage::count).reversed())
            .limit(10)
            .toList();

        return new DecisionAnalyticsResponse(
            totalDecisions,
            passRate,
            avgPredictionError,
            avgLatency,
            avgCost,
            topTools
        );
    }
}
