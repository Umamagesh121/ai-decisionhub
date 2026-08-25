package com.aidecisionhub.backend.service;

import com.aidecisionhub.backend.dto.RequestTraceResponse;
import com.aidecisionhub.backend.entity.*;
import com.aidecisionhub.backend.exception.NotFoundException;
import com.aidecisionhub.backend.repository.*;
import com.aidecisionhub.backend.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TraceQueryService {

    private final RequestRepository requestRepository;
    private final TaskRepository taskRepository;
    private final DecisionRepository decisionRepository;
    private final ExecutionRepository executionRepository;
    private final VerificationRepository verificationRepository;
    private final DecisionOutcomeRepository decisionOutcomeRepository;
    private final JsonUtil jsonUtil;

    public TraceQueryService(
        RequestRepository requestRepository,
        TaskRepository taskRepository,
        DecisionRepository decisionRepository,
        ExecutionRepository executionRepository,
        VerificationRepository verificationRepository,
        DecisionOutcomeRepository decisionOutcomeRepository,
        JsonUtil jsonUtil
    ) {
        this.requestRepository = requestRepository;
        this.taskRepository = taskRepository;
        this.decisionRepository = decisionRepository;
        this.executionRepository = executionRepository;
        this.verificationRepository = verificationRepository;
        this.decisionOutcomeRepository = decisionOutcomeRepository;
        this.jsonUtil = jsonUtil;
    }

    public RequestTraceResponse getTrace(UUID requestId) {
        RequestEntity request = requestRepository.findById(requestId)
            .orElseThrow(() -> new NotFoundException("Request not found: " + requestId));

        List<TaskEntity> tasks = taskRepository.findByRequestIdOrderByCreatedAtAsc(requestId);
        List<UUID> taskIds = tasks.stream().map(TaskEntity::getId).toList();

        List<DecisionEntity> decisions = taskIds.isEmpty() ? List.of() : decisionRepository.findByTaskIdIn(taskIds);
        Map<UUID, DecisionEntity> decisionByTaskId = decisions.stream()
            .collect(Collectors.toMap(d -> d.getTask().getId(), Function.identity()));

        List<UUID> decisionIds = decisions.stream().map(DecisionEntity::getId).toList();
        List<ExecutionEntity> executions = decisionIds.isEmpty() ? List.of() : executionRepository.findByDecisionIdIn(decisionIds);
        Map<UUID, ExecutionEntity> executionByDecisionId = executions.stream()
            .collect(Collectors.toMap(e -> e.getDecision().getId(), Function.identity()));

        List<UUID> executionIds = executions.stream().map(ExecutionEntity::getId).toList();
        List<VerificationEntity> verifications = executionIds.isEmpty() ? List.of() : verificationRepository.findByExecutionIdIn(executionIds);

        List<DecisionOutcomeEntity> outcomes = decisionIds.isEmpty() ? List.of() : decisionOutcomeRepository.findByDecisionIdIn(decisionIds);

        List<RequestTraceResponse.TaskItem> taskItems = tasks.stream()
            .map(task -> new RequestTraceResponse.TaskItem(
                task.getId(),
                task.getParentTaskId(),
                task.getTaskType().name(),
                task.getStatus().name(),
                jsonUtil.toMap(task.getTaskInputJson()),
                jsonUtil.toMap(task.getTaskOutputJson()),
                task.getCreatedAt()
            ))
            .toList();

        List<RequestTraceResponse.DecisionItem> decisionItems = decisions.stream()
            .map(decision -> new RequestTraceResponse.DecisionItem(
                decision.getId(),
                decision.getTask().getId(),
                decision.getChosenTool(),
                jsonUtil.toMap(decision.getCandidatesJson()),
                decision.getCostScore(),
                decision.getQualityScore(),
                decision.getSpeedScore(),
                decision.getRiskScore(),
                decision.getFinalScore(),
                decision.isRequiresApproval(),
                decision.getApprovedBy(),
                decision.getCreatedAt()
            ))
            .toList();

        List<RequestTraceResponse.ExecutionItem> executionItems = executions.stream()
            .map(execution -> new RequestTraceResponse.ExecutionItem(
                execution.getId(),
                execution.getDecision().getId(),
                execution.getActualCost(),
                execution.getActualLatencyMs(),
                jsonUtil.toMap(execution.getRawOutputJson()),
                execution.getStartedAt(),
                execution.getCompletedAt()
            ))
            .toList();

        List<RequestTraceResponse.VerificationItem> verificationItems = verifications.stream()
            .map(verification -> new RequestTraceResponse.VerificationItem(
                verification.getId(),
                verification.getExecution().getId(),
                verification.isPassed(),
                verification.getVerificationScore(),
                verification.getNotes(),
                verification.getCreatedAt()
            ))
            .toList();

        List<RequestTraceResponse.OutcomeItem> outcomeItems = outcomes.stream()
            .map(outcome -> new RequestTraceResponse.OutcomeItem(
                outcome.getId(),
                outcome.getDecision().getId(),
                outcome.getPredictedScore(),
                outcome.getActualQuality(),
                outcome.getPredictionError(),
                jsonUtil.toMap(outcome.getLearnedWeightAdjustmentJson()),
                outcome.getCreatedAt()
            ))
            .toList();

        return new RequestTraceResponse(
            request.getId(),
            request.getStatus().name(),
            request.getRawInput(),
            jsonUtil.toMap(request.getRequirementSpecJson()),
            taskItems,
            decisionItems,
            executionItems,
            verificationItems,
            outcomeItems
        );
    }

    public Optional<RequestTraceResponse.DecisionItem> getDecisionByTaskId(UUID taskId) {
        return decisionRepository.findByTaskId(taskId)
            .map(decision -> new RequestTraceResponse.DecisionItem(
                decision.getId(),
                decision.getTask().getId(),
                decision.getChosenTool(),
                jsonUtil.toMap(decision.getCandidatesJson()),
                decision.getCostScore(),
                decision.getQualityScore(),
                decision.getSpeedScore(),
                decision.getRiskScore(),
                decision.getFinalScore(),
                decision.isRequiresApproval(),
                decision.getApprovedBy(),
                decision.getCreatedAt()
            ));
    }
}
