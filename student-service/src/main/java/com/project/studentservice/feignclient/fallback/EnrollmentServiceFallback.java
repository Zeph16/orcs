package com.project.studentservice.feignclient.fallback;

import com.project.studentservice.feignclient.client.EnrollmentServiceClient;

public class EnrollmentServiceFallback implements EnrollmentServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach enrollment-service /demo/hi endpoint!";
    }
}