package com.decisionhub.repository;

import com.decisionhub.entity.DecisionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionHistoryRepository extends JpaRepository<DecisionHistory, Long> {
    List<DecisionHistory> findByDecisionIdOrderByCreatedAtDesc(Long decisionId);
    List<DecisionHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
}