package com.project.studentservice.feignclient.fallback;

import com.project.studentservice.feignclient.client.PaymentServiceClient;

public class PaymentServiceFallback implements PaymentServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach payment-service /demo/hi endpoint!";
    }
}
