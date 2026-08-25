package com.aidecisionhub.backend.service;

import com.aidecisionhub.backend.model.TaskType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class TaskDecomposerService {

    public List<TaskType> decompose(String rawInput, Map<String, Object> requirementSpec) {
        String lower = rawInput == null ? "" : rawInput.toLowerCase(Locale.ROOT);
        List<TaskType> taskTypes = new ArrayList<>();

        if (lower.contains("search") || lower.contains("research") || lower.contains("find")) {
            taskTypes.add(TaskType.RETRIEVAL);
        }
        if (lower.contains("classif") || lower.contains("categor") || lower.contains("label")) {
            taskTypes.add(TaskType.CLASSIFICATION);
        }
        if (lower.contains("build") || lower.contains("develop") || lower.contains("implement") || lower.contains("code")) {
            taskTypes.add(TaskType.CODE_EXEC);
        }
        if (lower.contains("summar")) {
            taskTypes.add(TaskType.SUMMARIZATION);
        }

        if (taskTypes.isEmpty()) {
            taskTypes.add(TaskType.GENERATION);
        }

        if (!taskTypes.contains(TaskType.VALIDATION)) {
            taskTypes.add(TaskType.VALIDATION);
        }

        return taskTypes;
    }
}
