package com.decisionhub.service;

import com.decisionhub.dto.*;
import com.decisionhub.entity.*;
import com.decisionhub.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DecisionService {

    private final DecisionRepository decisionRepository;
    private final DecisionOptionRepository optionRepository;
    private final DecisionFactorRepository factorRepository;
    private final OptionScoreRepository optionScoreRepository;
    private final FactorWeightRepository factorWeightRepository;
    private final ScoringEngine scoringEngine;

    // --- CRUD ---

    public List<Decision> getUserDecisions(Long userId, String status) {
        if (status != null && !status.isEmpty()) {
            return decisionRepository.findByUserIdAndStatus(userId, status);
        }
        return decisionRepository.findByUserId(userId);
    }

    public Decision getDecision(Long decisionId) {
        return decisionRepository.findById(decisionId)
                .orElseThrow(() -> new EntityNotFoundException("Decision not found: " + decisionId));
    }

    public Decision getDecisionForUser(Long decisionId, Long userId) {
        Decision decision = getDecision(decisionId);
        if (!decision.getUser().getId().equals(userId)) {
            throw new EntityNotFoundException("Decision not found: " + decisionId);
        }
        return decision;
    }

    @Transactional
    public Decision createDecision(DecisionRequest request, User user) {
        Decision decision = Decision.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .urgency(request.getUrgency() != null ? request.getUrgency() : "MEDIUM")
                .budget(request.getBudget())
                .deadline(request.getDeadline())
                .status("DRAFT")
                .build();
        return decisionRepository.save(decision);
    }

    @Transactional
    public Decision updateDecision(Long decisionId, DecisionRequest request, Long userId) {
        Decision decision = getDecisionForUser(decisionId, userId);
        if (request.getTitle() != null) decision.setTitle(request.getTitle());
        if (request.getDescription() != null) decision.setDescription(request.getDescription());
        if (request.getCategory() != null) decision.setCategory(request.getCategory());
        if (request.getUrgency() != null) decision.setUrgency(request.getUrgency());
        if (request.getBudget() != null) decision.setBudget(request.getBudget());
        if (request.getDeadline() != null) decision.setDeadline(request.getDeadline());
        return decisionRepository.save(decision);
    }

    @Transactional
    public void deleteDecision(Long decisionId, Long userId) {
        Decision decision = getDecisionForUser(decisionId, userId);
        decisionRepository.delete(decision);
    }

    // --- Options ---

    @Transactional
    public DecisionOption addOption(Long decisionId, OptionRequest request, Long userId) {
        Decision decision = getDecisionForUser(decisionId, userId);
        DecisionOption option = DecisionOption.builder()
                .decision(decision)
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return optionRepository.save(option);
    }

    // --- Factors ---

    @Transactional
    public DecisionFactor addFactor(Long decisionId, FactorRequest request, Long userId) {
        Decision decision = getDecisionForUser(decisionId, userId);
        DecisionFactor factor = DecisionFactor.builder()
                .decision(decision)
                .name(request.getName())
                .description(request.getDescription())
                .build();
        return factorRepository.save(factor);
    }

    // --- Weights ---

    @Transactional
    public void updateWeights(Long decisionId, List<WeightRequest> weights, Long userId) {
        Decision decision = getDecisionForUser(decisionId, userId);

        factorWeightRepository.deleteByDecisionId(decisionId);

        for (WeightRequest wr : weights) {
            DecisionFactor factor = factorRepository.findById(wr.getFactorId())
                    .orElseThrow(() -> new EntityNotFoundException("Factor not found: " + wr.getFactorId()));

            FactorWeight fw = FactorWeight.builder()
                    .decision(decision)
                    .factor(factor)
                    .weight(wr.getWeight())
                    .build();
            factorWeightRepository.save(fw);
        }

        // Recalculate
        scoringEngine.calculateWeightedScores(decisionId);
    }

    // --- Scores ---

    @Transactional
    public void setScores(Long decisionId, List<ScoreRequest> scores, Long userId) {
        Decision decision = getDecisionForUser(decisionId, userId);

        for (ScoreRequest sr : scores) {
            DecisionOption option = optionRepository.findById(sr.getOptionId())
                    .orElseThrow(() -> new EntityNotFoundException("Option not found: " + sr.getOptionId()));
            DecisionFactor factor = factorRepository.findById(sr.getFactorId())
                    .orElseThrow(() -> new EntityNotFoundException("Factor not found: " + sr.getFactorId()));

            var existing = optionScoreRepository.findByOptionIdAndFactorId(sr.getOptionId(), sr.getFactorId());
            if (existing.isPresent()) {
                OptionScore os = existing.get();
                os.setScore(sr.getScore());
                optionScoreRepository.save(os);
            } else {
                OptionScore os = OptionScore.builder()
                        .decision(decision)
                        .option(option)
                        .factor(factor)
                        .score(sr.getScore())
                        .build();
                optionScoreRepository.save(os);
            }
        }

        // Recalculate
        scoringEngine.calculateWeightedScores(decisionId);
    }

    // --- Comparison ---

    public ComparisonResponse getComparison(Long decisionId, Long userId) {
        getDecisionForUser(decisionId, userId);
        return scoringEngine.compare(decisionId);
    }

    // --- Simulation ---

    public SimulationResponse simulate(Long decisionId, SimulationRequest request, Long userId) {
        getDecisionForUser(decisionId, userId);
        return scoringEngine.simulate(decisionId, request);
    }
}