package com.project.studentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchRequestDTO {
    @NotNull(message = "Program ID is required")
    private Long programId;

    @NotNull(message = "Department ID is required")
    private Long departmentId;

    private String code;

    private Character section;

    @NotNull(message = "Expected Graduation Date is required")
    private LocalDate expectedGradDate;

    @Positive(message = "Credit cost must be positive")
    private Double creditCost;
}