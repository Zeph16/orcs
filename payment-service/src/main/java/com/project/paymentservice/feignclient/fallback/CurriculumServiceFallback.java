package com.project.paymentservice.feignclient.fallback;

import com.project.paymentservice.feignclient.client.CurriculumServiceClient;

public class CurriculumServiceFallback implements CurriculumServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach curriculum-service /demo/hi endpoint!";
    }
}