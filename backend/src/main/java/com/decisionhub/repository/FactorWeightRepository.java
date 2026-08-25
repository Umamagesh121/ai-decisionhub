package com.decisionhub.repository;

import com.decisionhub.entity.FactorWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FactorWeightRepository extends JpaRepository<FactorWeight, Long> {
    List<FactorWeight> findByDecisionId(Long decisionId);
    Optional<FactorWeight> findByDecisionIdAndFactorId(Long decisionId, Long factorId);
    void deleteByDecisionId(Long decisionId);
}