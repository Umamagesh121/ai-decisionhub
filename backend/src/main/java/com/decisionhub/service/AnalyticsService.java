package com.decisionhub.service;

import com.decisionhub.dto.DashboardResponse;
import com.decisionhub.entity.*;
import com.decisionhub.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final DecisionRepository decisionRepository;
    private final DecisionOptionRepository optionRepository;
    private final DecisionOutcomeRepository outcomeRepository;
    private final AiAnalysisRepository aiAnalysisRepository;

    public DashboardResponse getDashboard(Long userId) {
        List<Decision> allDecisions = decisionRepository.findByUserId(userId);
        long total = allDecisions.size();
        long active = allDecisions.stream().filter(d -> "ACTIVE".equals(d.getStatus()) || "DRAFT".equals(d.getStatus())).count();
        long completed = allDecisions.stream().filter(d -> "COMPLETED".equals(d.getStatus())).count();

        double avgConfidence = allDecisions.stream()
                .mapToDouble(Decision::getConfidenceScore)
                .average().orElse(0.0);

        long totalOutcomes = outcomeRepository.findAll().stream()
                .filter(o -> allDecisions.stream().anyMatch(d -> d.getId().equals(o.getDecision().getId())))
                .count();
        long successfulOutcomes = outcomeRepository.findAll().stream()
                .filter(o -> allDecisions.stream().anyMatch(d -> d.getId().equals(o.getDecision().getId())))
                .filter(o -> Boolean.TRUE.equals(o.getSuccess()))
                .count();
        double successRate = totalOutcomes > 0 ? (double) successfulOutcomes / totalOutcomes * 100 : 0;

        List<DashboardResponse.DecisionSummary> recent = allDecisions.stream()
                .sorted(Comparator.comparing(Decision::getUpdatedAt).reversed())
                .limit(5)
                .map(d -> {
                    String recommended = null;
                    if (d.getRecommendedOptionId() != null) {
                        recommended = optionRepository.findById(d.getRecommendedOptionId())
                                .map(DecisionOption::getName).orElse(null);
                    }
                    return DashboardResponse.DecisionSummary.builder()
                            .id(d.getId()).title(d.getTitle())
                            .status(d.getStatus()).category(d.getCategory())
                            .confidenceScore(d.getConfidenceScore())
                            .recommendedOption(recommended).build();
                })
                .collect(Collectors.toList());

        Map<String, Long> categoryDist = allDecisions.stream()
                .filter(d -> d.getCategory() != null)
                .collect(Collectors.groupingBy(Decision::getCategory, Collectors.counting()));

        return DashboardResponse.builder()
                .totalDecisions(total).activeDecisions(active).completedDecisions(completed)
                .avgConfidence(Math.round(avgConfidence * 100.0) / 100.0)
                .successRate(Math.round(successRate * 10.0) / 10.0)
                .recentDecisions(recent)
                .categoryDistribution(categoryDist)
                .build();
    }

    public List<Map<String, Object>> getInsights(Long userId) {
        List<Map<String, Object>> insights = new ArrayList<>();

        List<Decision> decisions = decisionRepository.findByUserId(userId);
        List<DecisionOutcome> outcomes = outcomeRepository.findAll().stream()
                .filter(o -> decisions.stream().anyMatch(d -> d.getId().equals(o.getDecision().getId())))
                .toList();

        Map<String, Object> insight1 = new LinkedHashMap<>();
        insight1.put("type", "SUCCESS_RATE");
        insight1.put("title", "Decision Success Pattern");
        long success = outcomes.stream().filter(o -> Boolean.TRUE.equals(o.getSuccess())).count();
        insight1.put("value", outcomes.isEmpty() ? "No outcomes yet" :
                String.format("%.0f%% of recorded outcomes were successful", (double) success / outcomes.size() * 100));
        insights.add(insight1);

        Map<String, Object> insight2 = new LinkedHashMap<>();
        insight2.put("type", "CATEGORY_INSIGHT");
        insight2.put("title", "Most Active Category");
        var topCat = decisions.stream()
                .filter(d -> d.getCategory() != null)
                .collect(Collectors.groupingBy(Decision::getCategory, Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);
        insight2.put("value", topCat != null ? topCat.getKey() + " (" + topCat.getValue() + " decisions)" : "N/A");
        insights.add(insight2);

        Map<String, Object> insight3 = new LinkedHashMap<>();
        insight3.put("type", "URGENCY_PATTERN");
        insight3.put("title", "Decision Urgency Distribution");
        var urgencyDist = decisions.stream()
                .collect(Collectors.groupingBy(Decision::getUrgency, Collectors.counting()));
        insight3.put("value", urgencyDist);
        insights.add(insight3);

        return insights;
    }

    public List<Map<String, Object>> getTrends(Long userId) {
        List<Map<String, Object>> trends = new ArrayList<>();

        List<Decision> decisions = decisionRepository.findByUserId(userId);

        // Monthly creation counts
        var monthlyCounts = decisions.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getCreatedAt().getYear() + "-" + String.format("%02d", d.getCreatedAt().getMonthValue()),
                        Collectors.counting()));

        Map<String, Object> trend1 = new LinkedHashMap<>();
        trend1.put("type", "MONTHLY_CREATION");
        trend1.put("title", "Decisions Created Per Month");
        trend1.put("data", monthlyCounts);
        trends.add(trend1);

        // Status distribution
        var statusDist = decisions.stream()
                .collect(Collectors.groupingBy(Decision::getStatus, Collectors.counting()));
        Map<String, Object> trend2 = new LinkedHashMap<>();
        trend2.put("type", "STATUS_DISTRIBUTION");
        trend2.put("title", "Current Decision Status Breakdown");
        trend2.put("data", statusDist);
        trends.add(trend2);

        // Average confidence trend
        var avgConfByMonth = decisions.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getCreatedAt().getYear() + "-" + String.format("%02d", d.getCreatedAt().getMonthValue()),
                        Collectors.averagingDouble(Decision::getConfidenceScore)));
        Map<String, Object> trend3 = new LinkedHashMap<>();
        trend3.put("type", "CONFIDENCE_TREND");
        trend3.put("title", "Average Confidence Score Trends");
        trend3.put("data", avgConfByMonth);
        trends.add(trend3);

        return trends;
    }
}