package com.aidecisionhub.backend.repository;

import com.aidecisionhub.backend.entity.DecisionOutcomeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface DecisionOutcomeRepository extends JpaRepository<DecisionOutcomeEntity, UUID> {
    List<DecisionOutcomeEntity> findByDecisionIdIn(Collection<UUID> decisionIds);
}
