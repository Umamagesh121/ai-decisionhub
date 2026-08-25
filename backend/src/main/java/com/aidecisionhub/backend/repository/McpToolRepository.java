package com.aidecisionhub.backend.repository;

import com.aidecisionhub.backend.entity.McpToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface McpToolRepository extends JpaRepository<McpToolEntity, UUID> {
    List<McpToolEntity> findByActiveTrue();
    Optional<McpToolEntity> findByName(String name);
}
