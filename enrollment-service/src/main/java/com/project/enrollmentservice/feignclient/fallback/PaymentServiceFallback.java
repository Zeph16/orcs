package com.project.enrollmentservice.feignclient.fallback;

import com.project.enrollmentservice.feignclient.client.PaymentServiceClient;

public class PaymentServiceFallback implements PaymentServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach payment-service /demo/hi endpoint!";
    }
}
