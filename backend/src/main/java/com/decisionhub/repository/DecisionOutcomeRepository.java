package com.decisionhub.repository;

import com.decisionhub.entity.DecisionOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionOutcomeRepository extends JpaRepository<DecisionOutcome, Long> {
    List<DecisionOutcome> findByDecisionId(Long decisionId);
    List<DecisionOutcome> findBySuccessTrue();
    long countBySuccessTrue();
    long countBySuccessFalse();
}