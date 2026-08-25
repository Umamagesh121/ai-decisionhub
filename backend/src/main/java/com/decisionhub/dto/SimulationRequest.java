package com.decisionhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationRequest {
    private BigDecimal budget;
    private LocalDate deadline;
    private String riskTolerance; // LOW, MEDIUM, HIGH
    private Map<String, Double> factorWeights; // factor name -> adjusted weight
}