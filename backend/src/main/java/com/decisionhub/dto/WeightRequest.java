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
public class WeightRequest {

    @NotNull(message = "Factor ID is required")
    private Long factorId;

    @NotNull(message = "Weight is required")
    private Double weight;
}