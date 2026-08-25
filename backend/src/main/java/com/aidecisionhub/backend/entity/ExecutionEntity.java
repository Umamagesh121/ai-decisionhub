package com.aidecisionhub.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "executions")
public class ExecutionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id", nullable = false)
    @JsonIgnore
    private DecisionEntity decision;

    @Column(precision = 10, scale = 4)
    private BigDecimal actualCost;

    @Column
    private Integer actualLatencyMs;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String rawOutputJson;

    @Column(nullable = false)
    private Instant startedAt;

    @Column(nullable = false)
    private Instant completedAt;

    public UUID getId() { return id; }
    public DecisionEntity getDecision() { return decision; }
    public void setDecision(DecisionEntity decision) { this.decision = decision; }
    public BigDecimal getActualCost() { return actualCost; }
    public void setActualCost(BigDecimal actualCost) { this.actualCost = actualCost; }
    public Integer getActualLatencyMs() { return actualLatencyMs; }
    public void setActualLatencyMs(Integer actualLatencyMs) { this.actualLatencyMs = actualLatencyMs; }
    public String getRawOutputJson() { return rawOutputJson; }
    public void setRawOutputJson(String rawOutputJson) { this.rawOutputJson = rawOutputJson; }
    public Instant getStartedAt() { return startedAt; }
    public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
