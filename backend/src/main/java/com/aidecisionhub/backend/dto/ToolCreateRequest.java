package com.aidecisionhub.backend.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;
import java.util.Set;

public class ToolCreateRequest {

    @NotBlank
    private String name;

    @NotEmpty
    private Set<String> capabilityTags;

    @DecimalMin("0.0")
    private BigDecimal avgCost;

    @Min(1)
    private Integer avgLatencyMs;

    @DecimalMin("0.0")
    @DecimalMax("1.0")
    private BigDecimal reliabilityScore;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getCapabilityTags() {
        return capabilityTags;
    }

    public void setCapabilityTags(Set<String> capabilityTags) {
        this.capabilityTags = capabilityTags;
    }

    public BigDecimal getAvgCost() {
        return avgCost;
    }

    public void setAvgCost(BigDecimal avgCost) {
        this.avgCost = avgCost;
    }

    public Integer getAvgLatencyMs() {
        return avgLatencyMs;
    }

    public void setAvgLatencyMs(Integer avgLatencyMs) {
        this.avgLatencyMs = avgLatencyMs;
    }

    public BigDecimal getReliabilityScore() {
        return reliabilityScore;
    }

    public void setReliabilityScore(BigDecimal reliabilityScore) {
        this.reliabilityScore = reliabilityScore;
    }
}
