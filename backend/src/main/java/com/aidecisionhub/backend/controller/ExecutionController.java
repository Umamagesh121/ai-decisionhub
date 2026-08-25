package com.aidecisionhub.backend.controller;

import com.aidecisionhub.backend.entity.ExecutionEntity;
import com.aidecisionhub.backend.entity.VerificationEntity;
import com.aidecisionhub.backend.exception.NotFoundException;
import com.aidecisionhub.backend.repository.ExecutionRepository;
import com.aidecisionhub.backend.repository.VerificationRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/executions")
public class ExecutionController {

    private final ExecutionRepository executionRepository;
    private final VerificationRepository verificationRepository;

    public ExecutionController(ExecutionRepository executionRepository, VerificationRepository verificationRepository) {
        this.executionRepository = executionRepository;
        this.verificationRepository = verificationRepository;
    }

    @GetMapping("/{id}")
    public ExecutionEntity getExecution(@PathVariable UUID id) {
        return executionRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Execution not found: " + id));
    }

    @GetMapping("/{id}/verify")
    public VerificationEntity getVerificationByExecution(@PathVariable UUID id) {
        return verificationRepository.findByExecutionId(id)
            .orElseThrow(() -> new NotFoundException("Verification not found for execution: " + id));
    }
}
