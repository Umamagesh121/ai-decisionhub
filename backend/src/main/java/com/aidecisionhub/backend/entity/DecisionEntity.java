package com.aidecisionhub.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "decisions")
public class DecisionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    @JsonIgnore
    private TaskEntity task;

    @Column(nullable = false)
    private String chosenTool;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String candidatesJson;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal costScore;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal qualityScore;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal speedScore;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal riskScore;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal finalScore;

    @Column(nullable = false)
    private boolean requiresApproval;

    @Column
    private UUID approvedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public TaskEntity getTask() { return task; }
    public void setTask(TaskEntity task) { this.task = task; }
    public String getChosenTool() { return chosenTool; }
    public void setChosenTool(String chosenTool) { this.chosenTool = chosenTool; }
    public String getCandidatesJson() { return candidatesJson; }
    public void setCandidatesJson(String candidatesJson) { this.candidatesJson = candidatesJson; }
    public BigDecimal getCostScore() { return costScore; }
    public void setCostScore(BigDecimal costScore) { this.costScore = costScore; }
    public BigDecimal getQualityScore() { return qualityScore; }
    public void setQualityScore(BigDecimal qualityScore) { this.qualityScore = qualityScore; }
    public BigDecimal getSpeedScore() { return speedScore; }
    public void setSpeedScore(BigDecimal speedScore) { this.speedScore = speedScore; }
    public BigDecimal getRiskScore() { return riskScore; }
    public void setRiskScore(BigDecimal riskScore) { this.riskScore = riskScore; }
    public BigDecimal getFinalScore() { return finalScore; }
    public void setFinalScore(BigDecimal finalScore) { this.finalScore = finalScore; }
    public boolean isRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(boolean requiresApproval) { this.requiresApproval = requiresApproval; }
    public UUID getApprovedBy() { return approvedBy; }
    public void setApprovedBy(UUID approvedBy) { this.approvedBy = approvedBy; }
    public Instant getCreatedAt() { return createdAt; }
}
