package com.project.paymentservice.dto;

import com.project.paymentservice.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {
    private Long id;
    private BigDecimal amount;
    private String description;
    private PaymentStatus status;
    private String checkoutUrl;
    private String txRef;
    private Long studentId;
    private String rollbackReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
