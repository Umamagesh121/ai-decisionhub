package com.aidecisionhub.backend.repository;

import com.aidecisionhub.backend.entity.ExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExecutionRepository extends JpaRepository<ExecutionEntity, UUID> {
    Optional<ExecutionEntity> findByDecisionId(UUID decisionId);
    List<ExecutionEntity> findByDecisionIdIn(Collection<UUID> decisionIds);
}
