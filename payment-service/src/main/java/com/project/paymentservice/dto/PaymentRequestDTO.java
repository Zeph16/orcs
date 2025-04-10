package com.project.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {
    @NotNull(message = "Student ID is needed")
    private Long studentId;

    @NotEmpty(message = "Payment amounts are needed")
    private List<BigDecimal> amounts;

    @NotEmpty(message = "Offering ids are required")
    private List<Long> offeringIDs;

    @NotBlank(message = "Reason for payment is needed")
    private String description;
}
