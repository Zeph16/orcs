package com.project.paymentservice.chapa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapaPaymentRequest {
    private String amount;
    private String currency;
    private String email;
    private String first_name;
    private String last_name;
    private String phone_number;
    private String tx_ref;
    private String callback_url;
    private String return_url;
    private Customization customization;
}