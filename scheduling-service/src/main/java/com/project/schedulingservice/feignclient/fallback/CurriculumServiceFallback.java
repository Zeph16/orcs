package com.project.schedulingservice.feignclient.fallback;

import com.project.schedulingservice.feignclient.client.CurriculumServiceClient;

public class CurriculumServiceFallback implements CurriculumServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach curriculum-service /demo/hi endpoint!";
    }
}