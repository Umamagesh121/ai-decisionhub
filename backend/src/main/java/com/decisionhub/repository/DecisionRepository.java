package com.decisionhub.repository;

import com.decisionhub.entity.Decision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, Long> {
    List<Decision> findByUserId(Long userId);
    List<Decision> findByUserIdAndStatus(Long userId, String status);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, String status);
}