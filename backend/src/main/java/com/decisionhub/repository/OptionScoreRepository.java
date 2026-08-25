package com.decisionhub.repository;

import com.decisionhub.entity.OptionScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OptionScoreRepository extends JpaRepository<OptionScore, Long> {
    List<OptionScore> findByDecisionId(Long decisionId);
    List<OptionScore> findByOptionId(Long optionId);
    Optional<OptionScore> findByOptionIdAndFactorId(Long optionId, Long factorId);
    void deleteByDecisionId(Long decisionId);
}