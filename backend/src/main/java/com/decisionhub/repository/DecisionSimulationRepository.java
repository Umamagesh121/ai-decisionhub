package com.decisionhub.repository;

import com.decisionhub.entity.DecisionSimulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionSimulationRepository extends JpaRepository<DecisionSimulation, Long> {
    List<DecisionSimulation> findByDecisionId(Long decisionId);
}