package com.decisionhub.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "decision_simulations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionSimulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id", nullable = false)
    private Decision decision;

    @Column(columnDefinition = "TEXT")
    private String parameters;

    @Column(name = "before_scores", columnDefinition = "TEXT")
    private String beforeScores;

    @Column(name = "after_scores", columnDefinition = "TEXT")
    private String afterScores;

    @Column(name = "rank_changes", columnDefinition = "TEXT")
    private String rankChanges;

    @Column(name = "recommendation_changed")
    @Builder.Default
    private Boolean recommendationChanged = false;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}