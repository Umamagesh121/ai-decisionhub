package com.aidecisionhub.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verifications")
public class VerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "execution_id", nullable = false)
    @JsonIgnore
    private ExecutionEntity execution;

    @Column(nullable = false)
    private boolean passed;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal verificationScore;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public ExecutionEntity getExecution() { return execution; }
    public void setExecution(ExecutionEntity execution) { this.execution = execution; }
    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }
    public BigDecimal getVerificationScore() { return verificationScore; }
    public void setVerificationScore(BigDecimal verificationScore) { this.verificationScore = verificationScore; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
}
