package com.project.paymentservice.chapa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapaPaymentResponse {
    private String message;
    private String status;
    private ChapaPaymentData data;
}
