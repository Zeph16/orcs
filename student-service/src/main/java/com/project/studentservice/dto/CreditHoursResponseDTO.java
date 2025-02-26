package com.project.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreditHoursResponseDTO {
    private int totalCreditHours;
    private int remainingCreditHours;
}