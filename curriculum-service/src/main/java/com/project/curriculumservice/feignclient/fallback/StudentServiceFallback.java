package com.project.curriculumservice.feignclient.fallback;

import com.project.curriculumservice.feignclient.client.StudentServiceClient;

public class StudentServiceFallback implements StudentServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach student-service /demo/hi endpoint!";
    }
}
