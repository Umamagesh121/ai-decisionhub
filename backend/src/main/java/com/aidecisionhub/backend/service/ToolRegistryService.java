package com.aidecisionhub.backend.service;

import com.aidecisionhub.backend.entity.McpToolEntity;
import com.aidecisionhub.backend.model.TaskType;
import com.aidecisionhub.backend.repository.McpToolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ToolRegistryService {

    private final McpToolRepository mcpToolRepository;

    public ToolRegistryService(McpToolRepository mcpToolRepository) {
        this.mcpToolRepository = mcpToolRepository;
    }

    public List<McpToolEntity> listAll() {
        return mcpToolRepository.findAll();
    }

    public List<McpToolEntity> listActiveForTask(TaskType taskType) {
        String expected = taskType.name().toLowerCase(Locale.ROOT);

        List<McpToolEntity> matched = mcpToolRepository.findByActiveTrue().stream()
            .filter(tool -> {
                Set<String> tags = tool.getCapabilityTags().stream()
                    .map(v -> v.toLowerCase(Locale.ROOT))
                    .collect(Collectors.toSet());
                return tags.contains(expected) || tags.contains("general") || tags.contains("all");
            })
            .toList();

        if (!matched.isEmpty()) {
            return matched;
        }
        return mcpToolRepository.findByActiveTrue();
    }
}
