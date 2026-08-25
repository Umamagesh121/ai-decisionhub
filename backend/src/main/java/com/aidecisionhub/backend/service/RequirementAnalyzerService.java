package com.aidecisionhub.backend.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RequirementAnalyzerService {

    private static final Pattern BUDGET_PATTERN = Pattern.compile("(?i)(?:budget|cost)\\s*[:=]?\\s*\\$?(\\d+(?:\\.\\d+)?)");
    private static final Pattern HOURS_PATTERN = Pattern.compile("(?i)(\\d+)\\s*(?:hour|hr|hrs)");
    private static final Pattern DAYS_PATTERN = Pattern.compile("(?i)(\\d+)\\s*(?:day|days)");

    public Map<String, Object> analyze(String rawInput) {
        String text = rawInput == null ? "" : rawInput.trim();
        String lower = text.toLowerCase(Locale.ROOT);

        String intent = resolveIntent(lower);
        List<String> entities = extractEntities(text);
        Map<String, Object> constraints = extractConstraints(lower);
        String priority = resolvePriority(lower);

        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("intent", intent);
        spec.put("entities", entities);
        spec.put("constraints", constraints);
        spec.put("priority", priority);
        spec.put("ambiguityFlag", text.length() < 20 || entities.isEmpty());
        return spec;
    }

    private String resolveIntent(String lower) {
        if (lower.contains("summar")) return "summarization";
        if (lower.contains("classif") || lower.contains("categor")) return "classification";
        if (lower.contains("search") || lower.contains("research") || lower.contains("find")) return "retrieval";
        if (lower.contains("build") || lower.contains("develop") || lower.contains("implement") || lower.contains("code")) return "code_generation";
        return "general_generation";
    }

    private String resolvePriority(String lower) {
        if (lower.contains("urgent") || lower.contains("asap") || lower.contains("critical")) return "HIGH";
        if (lower.contains("low priority") || lower.contains("later")) return "LOW";
        return "MEDIUM";
    }

    private List<String> extractEntities(String text) {
        if (text.isBlank()) return List.of();

        Set<String> entities = new LinkedHashSet<>();

        Arrays.stream(text.split("[^A-Za-z0-9_-]+"))
            .filter(token -> token.length() > 2)
            .filter(token -> Character.isUpperCase(token.charAt(0)))
            .forEach(entities::add);

        Arrays.stream(text.split("\s+"))
            .filter(token -> token.startsWith("#") || token.startsWith("@"))
            .forEach(entities::add);

        return entities.stream().limit(12).collect(Collectors.toList());
    }

    private Map<String, Object> extractConstraints(String lower) {
        Map<String, Object> constraints = new LinkedHashMap<>();

        Matcher budget = BUDGET_PATTERN.matcher(lower);
        if (budget.find()) {
            constraints.put("budget", Double.parseDouble(budget.group(1)));
        }

        Matcher hours = HOURS_PATTERN.matcher(lower);
        if (hours.find()) {
            constraints.put("deadlineHours", Integer.parseInt(hours.group(1)));
        } else {
            Matcher days = DAYS_PATTERN.matcher(lower);
            if (days.find()) {
                constraints.put("deadlineHours", Integer.parseInt(days.group(1)) * 24);
            }
        }

        constraints.put("qualityBar", lower.contains("high quality") ? "HIGH" : "NORMAL");
        constraints.put("dataSensitivity", lower.contains("pii") || lower.contains("confidential") ? "HIGH" : "NORMAL");

        return constraints;
    }
}
