package com.decisionhub.repository;

import com.decisionhub.entity.DecisionFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionFactorRepository extends JpaRepository<DecisionFactor, Long> {
    List<DecisionFactor> findByDecisionId(Long decisionId);
    void deleteByDecisionId(Long decisionId);
}