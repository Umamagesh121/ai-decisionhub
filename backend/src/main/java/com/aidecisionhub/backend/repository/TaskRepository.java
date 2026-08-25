package com.aidecisionhub.backend.repository;

import com.aidecisionhub.backend.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {
    List<TaskEntity> findByRequestIdOrderByCreatedAtAsc(UUID requestId);
    List<TaskEntity> findByIdIn(Collection<UUID> ids);
}
