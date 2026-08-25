package com.aidecisionhub.backend.entity;

import com.aidecisionhub.backend.model.TaskStatus;
import com.aidecisionhub.backend.model.TaskType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    @JsonIgnore
    private RequestEntity request;

    @Column
    private UUID parentTaskId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.PENDING;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String taskInputJson;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String taskOutputJson;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public RequestEntity getRequest() { return request; }
    public void setRequest(RequestEntity request) { this.request = request; }
    public UUID getParentTaskId() { return parentTaskId; }
    public void setParentTaskId(UUID parentTaskId) { this.parentTaskId = parentTaskId; }
    public TaskType getTaskType() { return taskType; }
    public void setTaskType(TaskType taskType) { this.taskType = taskType; }
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public String getTaskInputJson() { return taskInputJson; }
    public void setTaskInputJson(String taskInputJson) { this.taskInputJson = taskInputJson; }
    public String getTaskOutputJson() { return taskOutputJson; }
    public void setTaskOutputJson(String taskOutputJson) { this.taskOutputJson = taskOutputJson; }
    public Instant getCreatedAt() { return createdAt; }
}
