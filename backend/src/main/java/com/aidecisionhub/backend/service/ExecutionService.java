package com.aidecisionhub.backend.service;

import com.aidecisionhub.backend.entity.DecisionEntity;
import com.aidecisionhub.backend.entity.TaskEntity;
import com.aidecisionhub.backend.util.JsonUtil;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ExecutionService {

    private final JsonUtil jsonUtil;

    public ExecutionService(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    public SimulationResult simulate(TaskEntity task, DecisionEntity decision) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int latencyMs = random.nextInt(450, 2200);
        BigDecimal cost = BigDecimal.valueOf(random.nextDouble(0.001, 0.025)).setScale(4, RoundingMode.HALF_UP);

        double risk = decision.getRiskScore().doubleValue();
        // Demo mode: mostly successful runs, but still allows occasional failure.
        boolean success = random.nextDouble() > Math.min(0.35, 0.08 + (risk * 0.20));

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("tool", decision.getChosenTool());
        payload.put("taskType", task.getTaskType().name());
        payload.put("status", success ? "success" : "failed");
        payload.put("summary", success
            ? "Task executed successfully in demo simulation."
            : "Execution failed and needs fallback candidate.");
        payload.put("nextAction", success ? "verify" : "retry_with_alternative");
        payload.put("generatedAt", Instant.now().toString());

        return new SimulationResult(cost, latencyMs, jsonUtil.toJson(payload), success);
    }

    public record SimulationResult(
        BigDecimal actualCost,
        int actualLatencyMs,
        String rawOutputJson,
        boolean success
    ) {}
}
