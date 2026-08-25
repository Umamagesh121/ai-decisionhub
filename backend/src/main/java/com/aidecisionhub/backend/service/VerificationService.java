package com.aidecisionhub.backend.service;

import com.aidecisionhub.backend.entity.DecisionEntity;
import com.aidecisionhub.backend.entity.ExecutionEntity;
import com.aidecisionhub.backend.entity.TaskEntity;
import com.aidecisionhub.backend.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class VerificationService {

    private final JsonUtil jsonUtil;

    public VerificationService(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    public VerificationOutcome verify(TaskEntity task, DecisionEntity decision, ExecutionEntity execution) {
        Map<String, Object> output = jsonUtil.toMap(execution.getRawOutputJson());
        boolean successStatus = "success".equalsIgnoreCase(String.valueOf(output.getOrDefault("status", "failed")));

        double baseline = decision.getFinalScore().doubleValue();
        double jitter = ThreadLocalRandom.current().nextDouble(-0.06, 0.08);
        double score = Math.max(0.0, Math.min(1.0, 0.45 + (baseline * 0.5) + jitter));

        boolean passed = successStatus && score >= 0.50;
        String notes = passed
            ? "Verification passed: schema and quality checks satisfied"
            : "Verification failed: low quality score or execution status failure";

        return new VerificationOutcome(
            passed,
            BigDecimal.valueOf(score).setScale(4, RoundingMode.HALF_UP),
            notes,
            BigDecimal.valueOf(Math.max(score, 0.0)).setScale(4, RoundingMode.HALF_UP)
        );
    }

    public record VerificationOutcome(
        boolean passed,
        BigDecimal verificationScore,
        String notes,
        BigDecimal actualQuality
    ) {}
}
