package com.aidecisionhub.backend.repository;

import com.aidecisionhub.backend.entity.DecisionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DecisionRepository extends JpaRepository<DecisionEntity, UUID> {
    Optional<DecisionEntity> findByTaskId(UUID taskId);
    List<DecisionEntity> findByTaskIdIn(Collection<UUID> taskIds);
}
