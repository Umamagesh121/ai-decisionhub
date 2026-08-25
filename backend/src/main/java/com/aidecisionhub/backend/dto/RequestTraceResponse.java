package com.aidecisionhub.backend.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record RequestTraceResponse(
    UUID requestId,
    String status,
    String rawInput,
    Map<String, Object> requirementSpec,
    List<TaskItem> tasks,
    List<DecisionItem> decisions,
    List<ExecutionItem> executions,
    List<VerificationItem> verifications,
    List<OutcomeItem> outcomes
) {
    public record TaskItem(
        UUID id,
        UUID parentTaskId,
        String taskType,
        String status,
        Map<String, Object> input,
        Map<String, Object> output,
        Instant createdAt
    ) {}

    public record DecisionItem(
        UUID id,
        UUID taskId,
        String chosenTool,
        Map<String, Object> candidates,
        BigDecimal costScore,
        BigDecimal qualityScore,
        BigDecimal speedScore,
        BigDecimal riskScore,
        BigDecimal finalScore,
        boolean requiresApproval,
        UUID approvedBy,
        Instant createdAt
    ) {}

    public record ExecutionItem(
        UUID id,
        UUID decisionId,
        BigDecimal actualCost,
        Integer actualLatencyMs,
        Map<String, Object> rawOutput,
        Instant startedAt,
        Instant completedAt
    ) {}

    public record VerificationItem(
        UUID id,
        UUID executionId,
        boolean passed,
        BigDecimal verificationScore,
        String notes,
        Instant createdAt
    ) {}

    public record OutcomeItem(
        UUID id,
        UUID decisionId,
        BigDecimal predictedScore,
        BigDecimal actualQuality,
        BigDecimal predictionError,
        Map<String, Object> learnedWeightAdjustment,
        Instant createdAt
    ) {}
}
