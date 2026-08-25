package com.aidecisionhub.backend.service;

import com.aidecisionhub.backend.entity.McpToolEntity;
import com.aidecisionhub.backend.entity.OrganizationEntity;
import com.aidecisionhub.backend.entity.UserEntity;
import com.aidecisionhub.backend.repository.McpToolRepository;
import com.aidecisionhub.backend.repository.OrganizationRepository;
import com.aidecisionhub.backend.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;

@Component
public class DataSeeder {

    private final McpToolRepository mcpToolRepository;
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;

    public DataSeeder(
        McpToolRepository mcpToolRepository,
        OrganizationRepository organizationRepository,
        UserRepository userRepository
    ) {
        this.mcpToolRepository = mcpToolRepository;
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void seed() {
        seedOrganizationAndUser();
        seedTools();
    }

    private void seedOrganizationAndUser() {
        if (organizationRepository.count() > 0) return;

        OrganizationEntity org = new OrganizationEntity();
        org.setName("AI DecisionHub Demo Org");
        org = organizationRepository.save(org);

        UserEntity user = new UserEntity();
        user.setOrganization(org);
        user.setEmail("demo@aidecisionhub.local");
        user.setRole("ADMIN");
        userRepository.save(user);
    }

    private void seedTools() {
        if (mcpToolRepository.count() > 0) return;

        mcpToolRepository.save(tool("gpt-5.3-codex", Set.of("generation", "code_exec", "summarization", "general"), 0.0120, 1150, 0.93));
        mcpToolRepository.save(tool("DeepSeek-V4-Pro", Set.of("generation", "classification", "retrieval", "general"), 0.0050, 1400, 0.89));
        mcpToolRepository.save(tool("gemini-1.5-pro", Set.of("retrieval", "summarization", "classification"), 0.0040, 900, 0.87));
        mcpToolRepository.save(tool("llama3-local", Set.of("classification", "summarization"), 0.0015, 700, 0.75));
    }

    private McpToolEntity tool(String name, Set<String> tags, double cost, int latency, double reliability) {
        McpToolEntity t = new McpToolEntity();
        t.setName(name);
        t.setCapabilityTags(tags);
        t.setAvgCost(BigDecimal.valueOf(cost));
        t.setAvgLatencyMs(latency);
        t.setReliabilityScore(BigDecimal.valueOf(reliability));
        t.setActive(true);
        return t;
    }
}
