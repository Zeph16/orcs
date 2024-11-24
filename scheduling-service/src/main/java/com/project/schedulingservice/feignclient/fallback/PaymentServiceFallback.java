package com.project.schedulingservice.feignclient.fallback;

import com.project.schedulingservice.feignclient.client.PaymentServiceClient;

public class PaymentServiceFallback implements PaymentServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach payment-service /demo/hi endpoint!";
    }
}
