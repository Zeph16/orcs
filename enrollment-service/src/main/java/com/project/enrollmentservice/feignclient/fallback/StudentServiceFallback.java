package com.project.enrollmentservice.feignclient.fallback;

import com.project.enrollmentservice.feignclient.client.StudentServiceClient;

public class StudentServiceFallback implements StudentServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach student-service /demo/hi endpoint!";
    }
}