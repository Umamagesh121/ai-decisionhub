package com.aidecisionhub.backend.controller;

import com.aidecisionhub.backend.dto.ApprovalRequest;
import com.aidecisionhub.backend.dto.RequestTraceResponse;
import com.aidecisionhub.backend.entity.DecisionEntity;
import com.aidecisionhub.backend.exception.NotFoundException;
import com.aidecisionhub.backend.repository.DecisionRepository;
import com.aidecisionhub.backend.service.TraceQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class DecisionController {

    private final DecisionRepository decisionRepository;
    private final TraceQueryService traceQueryService;

    public DecisionController(DecisionRepository decisionRepository, TraceQueryService traceQueryService) {
        this.decisionRepository = decisionRepository;
        this.traceQueryService = traceQueryService;
    }

    @GetMapping("/api/v1/tasks/{id}/decision")
    public RequestTraceResponse.DecisionItem getDecisionByTask(@PathVariable UUID id) {
        return traceQueryService.getDecisionByTaskId(id)
            .orElseThrow(() -> new NotFoundException("Decision not found for task: " + id));
    }

    @PostMapping("/api/v1/decisions/{id}/approve")
    public DecisionEntity approveDecision(@PathVariable UUID id, @RequestBody(required = false) ApprovalRequest approvalRequest) {
        DecisionEntity decision = decisionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Decision not found: " + id));

        decision.setRequiresApproval(false);
        if (approvalRequest != null) {
            decision.setApprovedBy(approvalRequest.getApprovedBy());
        }
        return decisionRepository.save(decision);
    }
}
