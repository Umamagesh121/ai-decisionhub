package com.aidecisionhub.backend.dto;

import java.util.UUID;

public class ApprovalRequest {
    private UUID approvedBy;

    public UUID getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(UUID approvedBy) {
        this.approvedBy = approvedBy;
    }
}
