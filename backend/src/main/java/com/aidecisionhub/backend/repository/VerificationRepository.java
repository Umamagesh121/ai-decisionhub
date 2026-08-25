package com.aidecisionhub.backend.repository;

import com.aidecisionhub.backend.entity.VerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VerificationRepository extends JpaRepository<VerificationEntity, UUID> {
    Optional<VerificationEntity> findByExecutionId(UUID executionId);
    List<VerificationEntity> findByExecutionIdIn(Collection<UUID> executionIds);
}
