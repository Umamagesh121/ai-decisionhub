package com.aidecisionhub.backend.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class SubmitRequestRequest {

    @NotBlank
    private String rawInput;

    private UUID userId;

    public String getRawInput() {
        return rawInput;
    }

    public void setRawInput(String rawInput) {
        this.rawInput = rawInput;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
