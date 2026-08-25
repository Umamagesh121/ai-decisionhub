package com.aidecisionhub.backend.service;

import com.aidecisionhub.backend.dto.RequestTraceResponse;
import com.aidecisionhub.backend.dto.SubmitRequestRequest;
import com.aidecisionhub.backend.entity.*;
import com.aidecisionhub.backend.model.RequestStatus;
import com.aidecisionhub.backend.model.TaskStatus;
import com.aidecisionhub.backend.model.TaskType;
import com.aidecisionhub.backend.repository.*;
import com.aidecisionhub.backend.util.JsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Service
public class RequestOrchestratorService {

    private final RequestRepository requestRepository;
    private final TaskRepository taskRepository;
    private final DecisionRepository decisionRepository;
    private final ExecutionRepository executionRepository;
    private final VerificationRepository verificationRepository;
    private final DecisionOutcomeRepository decisionOutcomeRepository;

    private final RequirementAnalyzerService requirementAnalyzerService;
    private final TaskDecomposerService taskDecomposerService;
    private final ToolRegistryService toolRegistryService;
    private final DecisionEngineService decisionEngineService;
    private final ExecutionService executionService;
    private final VerificationService verificationService;
    private final TraceQueryService traceQueryService;
    private final WorkflowEventPublisher eventPublisher;
    private final JsonUtil jsonUtil;

    @Value("${app.demo.auto-approve-high-risk:true}")
    private boolean autoApproveHighRisk;

    public RequestOrchestratorService(
        RequestRepository requestRepository,
        TaskRepository taskRepository,
        DecisionRepository decisionRepository,
        ExecutionRepository executionRepository,
        VerificationRepository verificationRepository,
        DecisionOutcomeRepository decisionOutcomeRepository,
        RequirementAnalyzerService requirementAnalyzerService,
        TaskDecomposerService taskDecomposerService,
        ToolRegistryService toolRegistryService,
        DecisionEngineService decisionEngineService,
        ExecutionService executionService,
        VerificationService verificationService,
        TraceQueryService traceQueryService,
        WorkflowEventPublisher eventPublisher,
        JsonUtil jsonUtil
    ) {
        this.requestRepository = requestRepository;
        this.taskRepository = taskRepository;
        this.decisionRepository = decisionRepository;
        this.executionRepository = executionRepository;
        this.verificationRepository = verificationRepository;
        this.decisionOutcomeRepository = decisionOutcomeRepository;
        this.requirementAnalyzerService = requirementAnalyzerService;
        this.taskDecomposerService = taskDecomposerService;
        this.toolRegistryService = toolRegistryService;
        this.decisionEngineService = decisionEngineService;
        this.executionService = executionService;
        this.verificationService = verificationService;
        this.traceQueryService = traceQueryService;
        this.eventPublisher = eventPublisher;
        this.jsonUtil = jsonUtil;
    }

    @Transactional
    public RequestTraceResponse submitAndExecute(SubmitRequestRequest requestPayload) {
        RequestEntity request = new RequestEntity();
        request.setRawInput(requestPayload.getRawInput());
        request.setUserId(requestPayload.getUserId());
        request.setStatus(RequestStatus.PENDING);
        request = requestRepository.save(request);

        eventPublisher.publish(request.getId(), "RECEIVED", "Request received");

        request.setStatus(RequestStatus.ANALYZING);
        requestRepository.save(request);
        eventPublisher.publish(request.getId(), "ANALYZING", "Analyzing requirement");

        Map<String, Object> requirementSpec = requirementAnalyzerService.analyze(request.getRawInput());
        request.setRequirementSpecJson(jsonUtil.toJson(requirementSpec));

        request.setStatus(RequestStatus.DECIDING);
        requestRepository.save(request);
        eventPublisher.publish(request.getId(), "DECOMPOSING", "Task decomposition started");

        List<TaskType> taskTypes = taskDecomposerService.decompose(request.getRawInput(), requirementSpec);

        List<TaskEntity> tasks = new ArrayList<>();
        UUID previousTaskId = null;
        for (TaskType taskType : taskTypes) {
            TaskEntity task = new TaskEntity();
            task.setRequest(request);
            task.setParentTaskId(previousTaskId);
            task.setTaskType(taskType);
            task.setStatus(TaskStatus.PENDING);
            task.setTaskInputJson(jsonUtil.toJson(Map.of(
                "taskType", taskType.name(),
                "contextIntent", requirementSpec.getOrDefault("intent", "general")
            )));
            task = taskRepository.save(task);
            tasks.add(task);
            previousTaskId = task.getId();
        }

        eventPublisher.publish(request.getId(), "DECIDING", "Selecting model/tool for each task");

        List<DecisionEntity> decisions = new ArrayList<>();
        for (TaskEntity task : tasks) {
            List<McpToolEntity> candidates = toolRegistryService.listActiveForTask(task.getTaskType());
            var selection = decisionEngineService.evaluate(task.getTaskType(), candidates);

            DecisionEntity decision = new DecisionEntity();
            decision.setTask(task);
            decision.setChosenTool(selection.winner().tool());
            decision.setCandidatesJson(jsonUtil.toJson(Map.of("ranked", selection.rankedCandidates())));
            decision.setCostScore(selection.winner().costScore());
            decision.setQualityScore(selection.winner().qualityScore());
            decision.setSpeedScore(selection.winner().speedScore());
            decision.setRiskScore(selection.winner().riskScore());
            decision.setFinalScore(selection.winner().finalScore());
            decision.setRequiresApproval(selection.requiresApproval());

            if (selection.requiresApproval() && autoApproveHighRisk) {
                decision.setRequiresApproval(false);
            }

            decision = decisionRepository.save(decision);
            decisions.add(decision);

            task.setStatus(TaskStatus.DECIDED);
            taskRepository.save(task);
        }

        request.setStatus(RequestStatus.EXECUTING);
        requestRepository.save(request);
        eventPublisher.publish(request.getId(), "EXECUTING", "Running workflow");

        boolean allPassed = true;
        for (DecisionEntity decision : decisions) {
            TaskEntity task = decision.getTask();
            task.setStatus(TaskStatus.EXECUTING);
            taskRepository.save(task);

            eventPublisher.publish(request.getId(), "TASK_EXECUTING", "Executing task " + task.getId());

            var sim = executionService.simulate(task, decision);

            ExecutionEntity execution = new ExecutionEntity();
            execution.setDecision(decision);
            execution.setActualCost(sim.actualCost());
            execution.setActualLatencyMs(sim.actualLatencyMs());
            execution.setRawOutputJson(sim.rawOutputJson());
            execution.setStartedAt(Instant.now());
            execution.setCompletedAt(Instant.now());
            execution = executionRepository.save(execution);

            task.setTaskOutputJson(sim.rawOutputJson());

            var verification = verificationService.verify(task, decision, execution);
            VerificationEntity verificationEntity = new VerificationEntity();
            verificationEntity.setExecution(execution);
            verificationEntity.setPassed(verification.passed());
            verificationEntity.setVerificationScore(verification.verificationScore());
            verificationEntity.setNotes(verification.notes());
            verificationRepository.save(verificationEntity);

            task.setStatus(verification.passed() ? TaskStatus.DONE : TaskStatus.FAILED);
            taskRepository.save(task);

            if (!verification.passed()) {
                allPassed = false;
            }

            DecisionOutcomeEntity outcome = new DecisionOutcomeEntity();
            outcome.setDecision(decision);
            outcome.setPredictedScore(decision.getFinalScore());
            outcome.setActualQuality(verification.actualQuality());
            BigDecimal error = decision.getFinalScore().subtract(verification.actualQuality()).abs().setScale(4, RoundingMode.HALF_UP);
            outcome.setPredictionError(error);

            Map<String, Object> adjustment = new LinkedHashMap<>();
            adjustment.put("increaseRiskWeight", error.doubleValue() > 0.20);
            adjustment.put("suggestion", error.doubleValue() > 0.20
                ? "Model overperformed/underperformed unexpectedly; review risk and quality weights"
                : "Current weight profile is stable");
            adjustment.put("errorBand", error);
            outcome.setLearnedWeightAdjustmentJson(jsonUtil.toJson(adjustment));
            decisionOutcomeRepository.save(outcome);

            eventPublisher.publish(request.getId(), "TASK_COMPLETED", "Task " + task.getId() + " -> " + task.getStatus());
        }

        request.setStatus(allPassed ? RequestStatus.DONE : RequestStatus.FAILED);
        requestRepository.save(request);
        eventPublisher.publish(request.getId(), allPassed ? "DONE" : "FAILED", "Workflow finished");

        return traceQueryService.getTrace(request.getId());
    }
}
