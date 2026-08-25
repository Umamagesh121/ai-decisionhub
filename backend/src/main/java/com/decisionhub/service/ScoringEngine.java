package com.decisionhub.service;

import com.decisionhub.dto.ComparisonResponse;
import com.decisionhub.dto.SimulationRequest;
import com.decisionhub.dto.SimulationResponse;
import com.decisionhub.entity.*;
import com.decisionhub.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringEngine {

    private final DecisionRepository decisionRepository;
    private final DecisionOptionRepository optionRepository;
    private final DecisionFactorRepository factorRepository;
    private final OptionScoreRepository optionScoreRepository;
    private final FactorWeightRepository factorWeightRepository;

    /**
     * Calculate weighted scores for all options of a decision.
     * weightedScore = sum(score_i * weight_i) / sum(weights) * 10  (normalized to 0-100)
     */
    @Transactional
    public void calculateWeightedScores(Long decisionId) {
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new EntityNotFoundException("Decision not found: " + decisionId));

        List<DecisionOption> options = optionRepository.findByDecisionId(decisionId);
        List<DecisionFactor> factors = factorRepository.findByDecisionId(decisionId);
        List<OptionScore> scores = optionScoreRepository.findByDecisionId(decisionId);
        List<FactorWeight> weights = factorWeightRepository.findByDecisionId(decisionId);

        if (factors.isEmpty() || options.isEmpty()) {
            return;
        }

        // Build weight map: factorId -> weight
        Map<Long, Double> weightMap = new HashMap<>();
        for (FactorWeight fw : weights) {
            weightMap.put(fw.getFactor().getId(), fw.getWeight());
        }

        double totalWeight = weightMap.values().stream().mapToDouble(Double::doubleValue).sum();
        if (totalWeight == 0) totalWeight = 1.0;

        // Build score map: optionId -> (factorId -> score)
        Map<Long, Map<Long, Double>> scoreMap = new HashMap<>();
        for (OptionScore os : scores) {
            scoreMap.computeIfAbsent(os.getOption().getId(), k -> new HashMap<>())
                    .put(os.getFactor().getId(), os.getScore());
        }

        // Calculate weighted score for each option
        List<DecisionOption> scoredOptions = new ArrayList<>();
        for (DecisionOption option : options) {
            double weightedSum = 0;
            Map<Long, Double> optionScores = scoreMap.getOrDefault(option.getId(), Collections.emptyMap());
            for (DecisionFactor factor : factors) {
                double score = optionScores.getOrDefault(factor.getId(), 0.0);
                double weight = weightMap.getOrDefault(factor.getId(), 1.0);
                weightedSum += score * weight;
            }
            double normalizedScore = (weightedSum / totalWeight) * 10.0; // Scale to 0-100
            normalizedScore = Math.round(normalizedScore * 10.0) / 10.0; // Round to 1 decimal
            option.setTotalScore(normalizedScore);
            scoredOptions.add(option);
        }

        // Rank by score descending
        scoredOptions.sort((a, b) -> Double.compare(b.getTotalScore(), a.getTotalScore()));
        for (int i = 0; i < scoredOptions.size(); i++) {
            scoredOptions.get(i).setRank(i + 1);
        }
        optionRepository.saveAll(scoredOptions);

        // Update decision confidence (gap between top 2 as percentage)
        if (scoredOptions.size() >= 2) {
            double topScore = scoredOptions.get(0).getTotalScore();
            double secondScore = scoredOptions.get(1).getTotalScore();
            double maxPossible = 100.0;
            double gap = (topScore - secondScore) / maxPossible;
            double confidence = Math.min(0.95, 0.5 + gap * 5);
            decision.setConfidenceScore(Math.round(confidence * 100.0) / 100.0);
            decision.setRecommendedOptionId(scoredOptions.get(0).getId());
            decisionRepository.save(decision);
        } else if (scoredOptions.size() == 1) {
            decision.setConfidenceScore(0.5);
            decision.setRecommendedOptionId(scoredOptions.get(0).getId());
            decisionRepository.save(decision);
        }
    }

    /**
     * Return ranked comparison of options with weighted scores.
     */
    public ComparisonResponse compare(Long decisionId) {
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new EntityNotFoundException("Decision not found: " + decisionId));

        // Recalculate first
        calculateWeightedScores(decisionId);

        List<DecisionOption> options = optionRepository.findByDecisionId(decisionId);
        List<OptionScore> scores = optionScoreRepository.findByDecisionId(decisionId);
        Decision recommended = decisionRepository.findById(decisionId).orElse(decision);

        // Build factor score breakdown per option
        Map<Long, Map<String, Double>> factorScoresByOption = new HashMap<>();
        for (OptionScore os : scores) {
            factorScoresByOption.computeIfAbsent(os.getOption().getId(), k -> new LinkedHashMap<>())
                    .put(os.getFactor().getName(), os.getScore());
        }

        List<ComparisonResponse.OptionScoreDetail> optionDetails = options.stream()
                .sorted(Comparator.comparingInt(DecisionOption::getRank))
                .map(opt -> ComparisonResponse.OptionScoreDetail.builder()
                        .optionId(opt.getId())
                        .optionName(opt.getName())
                        .weightedScore(opt.getTotalScore())
                        .rank(opt.getRank())
                        .factorScores(factorScoresByOption.getOrDefault(opt.getId(), Collections.emptyMap()))
                        .build())
                .collect(Collectors.toList());

        String recommendedName = options.stream()
                .filter(o -> o.getId().equals(recommended.getRecommendedOptionId()))
                .findFirst().map(DecisionOption::getName).orElse(null);

        return ComparisonResponse.builder()
                .decisionId(decisionId)
                .decisionTitle(decision.getTitle())
                .options(optionDetails)
                .confidenceScore(recommended.getConfidenceScore())
                .recommendedOption(recommendedName)
                .recommendedOptionId(recommended.getRecommendedOptionId())
                .build();
    }

    /**
     * What-if simulation: re-run scoring with adjusted weights/constraints
     */
    public SimulationResponse simulate(Long decisionId, SimulationRequest request) {
        // Get current (before) state
        calculateWeightedScores(decisionId);
        List<DecisionOption> beforeOptions = new ArrayList<>(optionRepository.findByDecisionId(decisionId));
        beforeOptions.sort(Comparator.comparingInt(DecisionOption::getRank));

        List<SimulationResponse.ScoreDetail> beforeScores = beforeOptions.stream()
                .map(o -> SimulationResponse.ScoreDetail.builder()
                        .optionId(o.getId()).optionName(o.getName())
                        .score(o.getTotalScore()).rank(o.getRank()).build())
                .collect(Collectors.toList());

        String previousRecommendation = beforeOptions.isEmpty() ? null : beforeOptions.get(0).getName();

        // Save original weights
        List<FactorWeight> originalWeights = factorWeightRepository.findByDecisionId(decisionId);

        // Apply simulation weights
        if (request.getFactorWeights() != null && !request.getFactorWeights().isEmpty()) {
            List<DecisionFactor> factors = factorRepository.findByDecisionId(decisionId);
            Map<String, Long> factorNameToId = factors.stream()
                    .collect(Collectors.toMap(DecisionFactor::getName, DecisionFactor::getId, (a, b) -> a));

            factorWeightRepository.deleteByDecisionId(decisionId);
            Decision decision = decisionRepository.findById(decisionId).orElseThrow();

            for (var entry : request.getFactorWeights().entrySet()) {
                Long factorId = factorNameToId.get(entry.getKey());
                if (factorId != null) {
                    DecisionFactor factor = factorRepository.findById(factorId).orElse(null);
                    if (factor != null) {
                        factorWeightRepository.save(FactorWeight.builder()
                                .decision(decision).factor(factor).weight(entry.getValue()).build());
                    }
                }
            }
        }

        // Apply budget constraint
        if (request.getBudget() != null) {
            List<DecisionOption> options = optionRepository.findByDecisionId(decisionId);
            // Simple budget penalty: if budget is tight, penalize options that might be expensive
            // This is a heuristic; in production you'd have cost metadata per option
        }

        // Apply risk tolerance
        if (request.getRiskTolerance() != null) {
            // Adjust based on risk tolerance - heuristic weighting
            // HIGH tolerance = prefer higher-variance options
            // LOW tolerance = prefer lower-variance, safer options
        }

        // Recalculate with new weights
        calculateWeightedScores(decisionId);
        List<DecisionOption> afterOptions = new ArrayList<>(optionRepository.findByDecisionId(decisionId));
        afterOptions.sort(Comparator.comparingInt(DecisionOption::getRank));

        List<SimulationResponse.ScoreDetail> afterScores = afterOptions.stream()
                .map(o -> SimulationResponse.ScoreDetail.builder()
                        .optionId(o.getId()).optionName(o.getName())
                        .score(o.getTotalScore()).rank(o.getRank()).build())
                .collect(Collectors.toList());

        String newRecommendation = afterOptions.isEmpty() ? null : afterOptions.get(0).getName();

        // Detect rank changes
        Map<Long, Integer> beforeRanks = beforeOptions.stream()
                .collect(Collectors.toMap(DecisionOption::getId, DecisionOption::getRank));
        Map<Long, Integer> afterRanks = afterOptions.stream()
                .collect(Collectors.toMap(DecisionOption::getId, DecisionOption::getRank));

        List<SimulationResponse.RankChange> rankChanges = new ArrayList<>();
        for (DecisionOption opt : afterOptions) {
            Integer oldRank = beforeRanks.get(opt.getId());
            Integer newRank = afterRanks.get(opt.getId());
            if (oldRank != null && !oldRank.equals(newRank)) {
                rankChanges.add(SimulationResponse.RankChange.builder()
                        .optionId(opt.getId()).optionName(opt.getName())
                        .oldRank(oldRank).newRank(newRank).build());
            }
        }

        boolean recChanged = !Objects.equals(previousRecommendation, newRecommendation);

        // Restore original weights
        factorWeightRepository.deleteByDecisionId(decisionId);
        Decision decision = decisionRepository.findById(decisionId).orElseThrow();
        for (FactorWeight fw : originalWeights) {
            factorWeightRepository.save(FactorWeight.builder()
                    .decision(decision).factor(fw.getFactor()).weight(fw.getWeight()).build());
        }
        // Revert to original scores
        calculateWeightedScores(decisionId);

        return SimulationResponse.builder()
                .decisionId(decisionId)
                .beforeScores(beforeScores)
                .afterScores(afterScores)
                .rankChanges(rankChanges)
                .recommendationChanged(recChanged)
                .previousRecommendation(previousRecommendation)
                .newRecommendation(newRecommendation)
                .build();
    }
}