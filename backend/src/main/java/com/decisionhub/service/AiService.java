package com.decisionhub.service;

import com.decisionhub.dto.ComparisonResponse;
import com.decisionhub.entity.*;
import com.decisionhub.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

    private final DecisionRepository decisionRepository;
    private final DecisionOptionRepository optionRepository;
    private final DecisionFactorRepository factorRepository;
    private final OptionScoreRepository optionScoreRepository;
    private final FactorWeightRepository factorWeightRepository;
    private final AiAnalysisRepository aiAnalysisRepository;
    private final AiRecommendationRepository aiRecommendationRepository;
    private final RiskAnalysisRepository riskAnalysisRepository;
    private final ScoringEngine scoringEngine;

    /**
     * Deterministic rule-based AI analysis (no real LLM).
     */
    @Transactional
    public AiAnalysis analyze(Long decisionId) {
        Decision decision = decisionRepository.findById(decisionId)
                .orElseThrow(() -> new EntityNotFoundException("Decision not found: " + decisionId));

        // Run scoring first
        ComparisonResponse comparison = scoringEngine.compare(decisionId);

        List<DecisionOption> options = optionRepository.findByDecisionId(decisionId);
        List<DecisionFactor> factors = factorRepository.findByDecisionId(decisionId);
        List<OptionScore> scores = optionScoreRepository.findByDecisionId(decisionId);
        List<FactorWeight> weights = factorWeightRepository.findByDecisionId(decisionId);

        StringBuilder content = new StringBuilder();
        content.append("=== AI DECISION ANALYSIS ===\n\n");
        content.append("Decision: ").append(decision.getTitle()).append("\n");
        content.append("Category: ").append(decision.getCategory()).append("\n");
        content.append("Urgency: ").append(decision.getUrgency()).append("\n\n");

        // Options ranked
        content.append("--- Ranked Options ---\n");
        var sortedOptions = options.stream()
                .sorted(Comparator.comparingInt(DecisionOption::getRank))
                .toList();

        for (DecisionOption opt : sortedOptions) {
            content.append(String.format("  #%d: %s (Score: %.1f/100)\n",
                    opt.getRank(), opt.getName(), opt.getTotalScore()));
        }

        // Top recommendation
        if (!options.isEmpty()) {
            DecisionOption top = sortedOptions.get(0);
            content.append("\n--- TOP RECOMMENDATION ---\n");
            content.append("Recommended: ").append(top.getName()).append("\n");
            content.append("Score: ").append(String.format("%.1f/100", top.getTotalScore())).append("\n");

            // Reasoning
            content.append("\nKey Insights:\n");
            if (sortedOptions.size() > 1) {
                double gap = top.getTotalScore() - sortedOptions.get(1).getTotalScore();
                if (gap > 20) {
                    content.append("- Strong winner with clear advantage (").append(String.format("%.1f", gap)).append(" points ahead)\n");
                } else if (gap > 5) {
                    content.append("- Moderate lead over alternatives (").append(String.format("%.1f", gap)).append(" points ahead)\n");
                } else {
                    content.append("- Close competition - consider both top options carefully\n");
                }
            }

            // Analyze top option's strengths
            content.append("\nStrengths of ").append(top.getName()).append(":\n");
            for (OptionScore os : scores) {
                if (os.getOption().getId().equals(top.getId()) && os.getScore() >= 8.0) {
                    double weight = weights.stream()
                            .filter(w -> w.getFactor().getId().equals(os.getFactor().getId()))
                            .findFirst().map(FactorWeight::getWeight).orElse(1.0);
                    content.append(String.format("  - %s: %.1f/10 (weight: %.1f)\n",
                            os.getFactor().getName(), os.getScore(), weight));
                }
            }

            // Weaknesses
            content.append("\nWeaknesses of ").append(top.getName()).append(":\n");
            for (OptionScore os : scores) {
                if (os.getOption().getId().equals(top.getId()) && os.getScore() <= 5.0) {
                    content.append(String.format("  - %s: %.1f/10\n", os.getFactor().getName(), os.getScore()));
                }
            }
        }

        // Confidence analysis
        double avgScore = options.stream().mapToDouble(DecisionOption::getTotalScore).average().orElse(0);
        double variance = options.stream()
                .mapToDouble(o -> Math.pow(o.getTotalScore() - avgScore, 2))
                .average().orElse(0);
        double normalizedConfidence = Math.min(0.95, Math.max(0.3, 1.0 - (Math.sqrt(variance) / 50.0)));
        normalizedConfidence = Math.round(normalizedConfidence * 100.0) / 100.0;

        content.append("\n--- CONFIDENCE ---\n");
        content.append("Overall confidence: ").append(String.format("%.0f%%", normalizedConfidence * 100)).append("\n");
        content.append("Score variance: ").append(String.format("%.1f", Math.sqrt(variance))).append("\n");

        if (normalizedConfidence >= 0.80) {
            content.append("Assessment: HIGH confidence in this recommendation.\n");
        } else if (normalizedConfidence >= 0.55) {
            content.append("Assessment: MODERATE confidence. Consider gathering more data.\n");
        } else {
            content.append("Assessment: LOW confidence. Additional analysis recommended.\n");
        }

        // Save analysis
        AiAnalysis analysis = AiAnalysis.builder()
                .decision(decision)
                .analysisType("COMPREHENSIVE")
                .content(content.toString())
                .confidence(normalizedConfidence)
                .modelUsed("Deterministic Rule Engine v1.0")
                .tokensUsed(content.length())
                .build();
        aiAnalysisRepository.save(analysis);

        // Save recommendation
        if (!options.isEmpty()) {
            DecisionOption top = sortedOptions.get(0);
            StringBuilder pros = new StringBuilder();
            StringBuilder cons = new StringBuilder();
            for (OptionScore os : scores) {
                if (os.getOption().getId().equals(top.getId())) {
                    if (os.getScore() >= 7.0) pros.append(os.getFactor().getName()).append("(").append(String.format("%.0f", os.getScore())).append("), ");
                    else if (os.getScore() <= 4.0) cons.append(os.getFactor().getName()).append("(").append(String.format("%.0f", os.getScore())).append("), ");
                }
            }

            String riskLevel = normalizedConfidence >= 0.8 ? "LOW" : normalizedConfidence >= 0.55 ? "MEDIUM" : "HIGH";

            AiRecommendation rec = AiRecommendation.builder()
                    .decision(decision)
                    .option(top)
                    .reasoning("Based on weighted multi-factor analysis, " + top.getName() +
                            " scores highest (" + String.format("%.1f", top.getTotalScore()) + "/100).")
                    .pros(pros.toString().replaceAll(", $", ""))
                    .cons(cons.toString().replaceAll(", $", ""))
                    .riskLevel(riskLevel)
                    .build();
            aiRecommendationRepository.save(rec);

            // Risk analysis per option
            for (DecisionOption opt : sortedOptions) {
                String riskDesc = generateRiskDescription(opt, scores, factors);
                String riskLevel2 = opt.getRank() == 1 ? "LOW" : opt.getRank() == 2 ? "MEDIUM" : "HIGH";

                RiskAnalysis ra = RiskAnalysis.builder()
                        .decision(decision)
                        .option(opt)
                        .riskType(opt.getRank() == 1 ? "Performance Risk" : "Selection Risk")
                        .riskDescription(riskDesc)
                        .probability(opt.getRank() == 1 ? "Low" : "Medium")
                        .impact(opt.getRank() == 1 ? "Low" : "Medium")
                        .mitigation("Monitor " + opt.getName() + " performance against key factors. Review quarterly.")
                        .build();
                riskAnalysisRepository.save(ra);
            }
        }

        return analysis;
    }

    private String generateRiskDescription(DecisionOption option, List<OptionScore> scores, List<DecisionFactor> factors) {
        double avgScore = scores.stream()
                .filter(s -> s.getOption().getId().equals(option.getId()))
                .mapToDouble(OptionScore::getScore)
                .average().orElse(5.0);
        if (avgScore >= 8.0) return option.getName() + " shows strong performance across factors. Risk is minimal.";
        if (avgScore >= 6.0) return option.getName() + " shows adequate performance with some weak areas to monitor.";
        return option.getName() + " has significant gaps that may impact outcomes. Risk is elevated.";
    }

    @Transactional
    public AiRecommendation getRecommendation(Long decisionId) {
        return aiRecommendationRepository.findTopByDecisionIdOrderByCreatedAtDesc(decisionId)
                .orElse(null);
    }
}