package com.aidecisionhub.backend.controller;

import com.aidecisionhub.backend.dto.ToolCreateRequest;
import com.aidecisionhub.backend.entity.McpToolEntity;
import com.aidecisionhub.backend.repository.McpToolRepository;
import com.aidecisionhub.backend.service.ToolRegistryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tools")
public class ToolController {

    private final ToolRegistryService toolRegistryService;
    private final McpToolRepository mcpToolRepository;

    public ToolController(ToolRegistryService toolRegistryService, McpToolRepository mcpToolRepository) {
        this.toolRegistryService = toolRegistryService;
        this.mcpToolRepository = mcpToolRepository;
    }

    @GetMapping
    public Object listTools() {
        return toolRegistryService.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public McpToolEntity registerTool(@Valid @RequestBody ToolCreateRequest request) {
        McpToolEntity tool = new McpToolEntity();
        tool.setName(request.getName());
        tool.setCapabilityTags(request.getCapabilityTags());
        tool.setAvgCost(request.getAvgCost());
        tool.setAvgLatencyMs(request.getAvgLatencyMs());
        tool.setReliabilityScore(request.getReliabilityScore());
        tool.setActive(true);
        return mcpToolRepository.save(tool);
    }
}
