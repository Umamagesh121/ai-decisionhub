package com.decisionhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "decision_outcomes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionOutcome {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id", nullable = false)
    private Decision decision;

    @Column(name = "actual_result", columnDefinition = "TEXT")
    private String actualResult;

    private Boolean success;

    @Column(name = "actual_cost", precision = 12, scale = 2)
    private BigDecimal actualCost;

    @Column(name = "expected_cost", precision = 12, scale = 2)
    private BigDecimal expectedCost;

    @Column(name = "actual_time_days")
    private Integer actualTimeDays;

    @Column(name = "expected_time_days")
    private Integer expectedTimeDays;

    private Integer satisfaction;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "recorded_at", updatable = false)
    @Builder.Default
    private LocalDateTime recordedAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        recordedAt = LocalDateTime.now();
    }
}