package com.project.paymentservice.chapa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChapaVerificationResponse {
    private String status;
    private ChapaVerificationData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChapaVerificationData {
        private String status;
    }
}

// Sample verification response just in case
//{
//    "message": "Payment details fetched successfully",
//    "status": "success",
//    "data": {
//        "first_name": "Test",
//        "last_name": "Student",
//        "email": "teststudent@email.com",
//        "phone_number": "+25199999999",
//        "currency": "ETB",
//        "amount": 200,
//        "charge": null,
//        "mode": "test",
//        "method": null,
//        "type": "Unknown",
//        "status": "pending",
//        "reference": null,
//        "tx_ref": "TX-44793c28-9",
//        "customization": {
//            "title": "HiLCoE",
//            "description": "some description",
//            "logo": null
//        },
//        "meta": null,
//        "created_at": "2025-02-08T14:05:33.000000Z",
//        "updated_at": "2025-02-08T14:05:33.000000Z"
//    }
//}