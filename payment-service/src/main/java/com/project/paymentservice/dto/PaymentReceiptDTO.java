package com.project.paymentservice.dto;

import com.project.paymentservice.model.PaymentStatus;
import com.project.paymentservice.model.PaymentType;
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
public class PaymentReceiptDTO {
    private String studentName;
    private String cardId;
    private String email;
    private String phone;
    private String description;
    private LocalDateTime paymentDate;
    private PaymentType type;
    private BigDecimal totalFee;
    private PaymentStatus status;
}
