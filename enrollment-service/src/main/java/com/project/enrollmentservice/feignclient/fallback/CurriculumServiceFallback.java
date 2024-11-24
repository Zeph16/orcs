package com.project.enrollmentservice.feignclient.fallback;

import com.project.enrollmentservice.feignclient.client.CurriculumServiceClient;

public class CurriculumServiceFallback implements CurriculumServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach curriculum-service /demo/hi endpoint!";
    }
}