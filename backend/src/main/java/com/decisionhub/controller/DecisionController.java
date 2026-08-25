package com.decisionhub.controller;

import com.decisionhub.dto.*;
import com.decisionhub.entity.*;
import com.decisionhub.service.AiService;
import com.decisionhub.service.DecisionService;
import com.decisionhub.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/decisions")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionService decisionService;
    private final AiService aiService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<List<Decision>> listDecisions(@RequestParam(required = false) String status) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(decisionService.getUserDecisions(userId, status));
    }

    @PostMapping
    public ResponseEntity<Decision> createDecision(@Valid @RequestBody DecisionRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        var user = securityUtils.getCurrentUser();
        return ResponseEntity.ok(decisionService.createDecision(request, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Decision> getDecision(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(decisionService.getDecisionForUser(id, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Decision> updateDecision(@PathVariable Long id, @Valid @RequestBody DecisionRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(decisionService.updateDecision(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDecision(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        decisionService.deleteDecision(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/options")
    public ResponseEntity<DecisionOption> addOption(@PathVariable Long id, @Valid @RequestBody OptionRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(decisionService.addOption(id, request, userId));
    }

    @PostMapping("/{id}/factors")
    public ResponseEntity<DecisionFactor> addFactor(@PathVariable Long id, @Valid @RequestBody FactorRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(decisionService.addFactor(id, request, userId));
    }

    @PutMapping("/{id}/weights")
    public ResponseEntity<Void> updateWeights(@PathVariable Long id, @Valid @RequestBody List<WeightRequest> weights) {
        Long userId = securityUtils.getCurrentUserId();
        decisionService.updateWeights(id, weights, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/scores")
    public ResponseEntity<Void> setScores(@PathVariable Long id, @Valid @RequestBody List<ScoreRequest> scores) {
        Long userId = securityUtils.getCurrentUserId();
        decisionService.setScores(id, scores, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/comparison")
    public ResponseEntity<ComparisonResponse> getComparison(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(decisionService.getComparison(id, userId));
    }

    @GetMapping("/{id}/recommendation")
    public ResponseEntity<?> getRecommendation(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        decisionService.getDecisionForUser(id, userId); // ownership check
        var rec = aiService.getRecommendation(id);
        return rec != null ? ResponseEntity.ok(rec) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/simulate")
    public ResponseEntity<SimulationResponse> simulate(@PathVariable Long id, @RequestBody SimulationRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(decisionService.simulate(id, request, userId));
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<?> analyze(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        decisionService.getDecisionForUser(id, userId); // ownership check
        var analysis = aiService.analyze(id);
        return ResponseEntity.ok(analysis);
    }

    @PostMapping("/{id}/outcome")
    public ResponseEntity<?> recordOutcome(@PathVariable Long id, @RequestBody DecisionOutcome outcome) {
        Long userId = securityUtils.getCurrentUserId();
        // In a real app, this would save the outcome; for now just acknowledge
        return ResponseEntity.ok().build();
    }
}