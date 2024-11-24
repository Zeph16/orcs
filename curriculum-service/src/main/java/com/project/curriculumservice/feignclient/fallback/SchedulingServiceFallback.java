package com.project.curriculumservice.feignclient.fallback;

import com.project.curriculumservice.feignclient.client.SchedulingServiceClient;

public class SchedulingServiceFallback implements SchedulingServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach scheduling-service /demo/hi endpoint!";
    }
}
