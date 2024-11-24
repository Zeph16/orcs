package com.project.paymentservice.feignclient.fallback;

import com.project.paymentservice.feignclient.client.SchedulingServiceClient;

public class SchedulingServiceFallback implements SchedulingServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach scheduling-service /demo/hi endpoint!";
    }
}
