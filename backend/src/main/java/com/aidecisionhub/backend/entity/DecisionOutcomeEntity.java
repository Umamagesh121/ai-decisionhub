package com.aidecisionhub.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "decision_outcomes")
public class DecisionOutcomeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id", nullable = false)
    @JsonIgnore
    private DecisionEntity decision;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal predictedScore;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal actualQuality;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal predictionError;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String learnedWeightAdjustmentJson;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public DecisionEntity getDecision() { return decision; }
    public void setDecision(DecisionEntity decision) { this.decision = decision; }
    public BigDecimal getPredictedScore() { return predictedScore; }
    public void setPredictedScore(BigDecimal predictedScore) { this.predictedScore = predictedScore; }
    public BigDecimal getActualQuality() { return actualQuality; }
    public void setActualQuality(BigDecimal actualQuality) { this.actualQuality = actualQuality; }
    public BigDecimal getPredictionError() { return predictionError; }
    public void setPredictionError(BigDecimal predictionError) { this.predictionError = predictionError; }
    public String getLearnedWeightAdjustmentJson() { return learnedWeightAdjustmentJson; }
    public void setLearnedWeightAdjustmentJson(String learnedWeightAdjustmentJson) { this.learnedWeightAdjustmentJson = learnedWeightAdjustmentJson; }
    public Instant getCreatedAt() { return createdAt; }
}
