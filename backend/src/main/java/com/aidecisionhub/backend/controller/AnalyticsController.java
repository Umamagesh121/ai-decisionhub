package com.aidecisionhub.backend.controller;

import com.aidecisionhub.backend.dto.DecisionAnalyticsResponse;
import com.aidecisionhub.backend.service.DecisionAnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final DecisionAnalyticsService decisionAnalyticsService;

    public AnalyticsController(DecisionAnalyticsService decisionAnalyticsService) {
        this.decisionAnalyticsService = decisionAnalyticsService;
    }

    @GetMapping("/decisions")
    public DecisionAnalyticsResponse getDecisionAnalytics() {
        return decisionAnalyticsService.getAnalytics();
    }
}
