package com.aidecisionhub.backend.repository;

import com.aidecisionhub.backend.entity.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RequestRepository extends JpaRepository<RequestEntity, UUID> {
}
