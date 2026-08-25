package com.decisionhub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DecisionRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String category;
    private String urgency;
    private BigDecimal budget;
    private LocalDate deadline;
}