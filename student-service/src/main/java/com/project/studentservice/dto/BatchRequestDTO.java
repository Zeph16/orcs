package com.project.studentservice.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;


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

    @NotNull(message = "Term ID is required")
    private Integer termId;

    @NotNull(message = "Section is required")
    private Character section;

    @NotNull(message = "Expected Graduation Date is required")
    private LocalDate expectedGradDate;

    @Positive(message = "Credit cost must be positive")
    private Double creditCost;
}