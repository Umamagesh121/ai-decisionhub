package com.decisionhub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreRequest {

    @NotNull(message = "Option ID is required")
    private Long optionId;

    @NotNull(message = "Factor ID is required")
    private Long factorId;

    @NotNull(message = "Score is required")
    private Double score;
}