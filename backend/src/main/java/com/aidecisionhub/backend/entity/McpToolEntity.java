package com.aidecisionhub.backend.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "mcp_tools")
public class McpToolEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "mcp_tool_capability_tags", joinColumns = @JoinColumn(name = "tool_id"))
    @Column(name = "capability_tag")
    private Set<String> capabilityTags = new HashSet<>();

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal avgCost;

    @Column(nullable = false)
    private Integer avgLatencyMs;

    @Column(nullable = false, precision = 8, scale = 4)
    private BigDecimal reliabilityScore;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Set<String> getCapabilityTags() { return capabilityTags; }
    public void setCapabilityTags(Set<String> capabilityTags) { this.capabilityTags = capabilityTags; }
    public BigDecimal getAvgCost() { return avgCost; }
    public void setAvgCost(BigDecimal avgCost) { this.avgCost = avgCost; }
    public Integer getAvgLatencyMs() { return avgLatencyMs; }
    public void setAvgLatencyMs(Integer avgLatencyMs) { this.avgLatencyMs = avgLatencyMs; }
    public BigDecimal getReliabilityScore() { return reliabilityScore; }
    public void setReliabilityScore(BigDecimal reliabilityScore) { this.reliabilityScore = reliabilityScore; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getCreatedAt() { return createdAt; }
}
