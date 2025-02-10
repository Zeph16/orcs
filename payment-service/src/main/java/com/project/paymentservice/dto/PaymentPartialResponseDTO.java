package com.project.paymentservice.dto;

import com.project.paymentservice.model.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentPartialResponseDTO {
    private Long id;
    private String checkout_url;
    private PaymentType type;
}