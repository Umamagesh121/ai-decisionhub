package com.decisionhub.controller;

import com.decisionhub.dto.DashboardResponse;
import com.decisionhub.service.AnalyticsService;
import com.decisionhub.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final SecurityUtils securityUtils;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> dashboard() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(analyticsService.getDashboard(userId));
    }

    @GetMapping("/insights")
    public ResponseEntity<List<Map<String, Object>>> insights() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(analyticsService.getInsights(userId));
    }

    @GetMapping("/trends")
    public ResponseEntity<List<Map<String, Object>>> trends() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(analyticsService.getTrends(userId));
    }
}