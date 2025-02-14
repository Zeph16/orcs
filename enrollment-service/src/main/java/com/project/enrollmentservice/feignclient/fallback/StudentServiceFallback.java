package com.project.enrollmentservice.feignclient.fallback;

import com.project.enrollmentservice.feignclient.client.StudentServiceClient;
import com.project.enrollmentservice.feignclient.dto.StudentResponseDTO;
import org.springframework.http.ResponseEntity;

public class StudentServiceFallback implements StudentServiceClient {

    @Override
    public ResponseEntity<StudentResponseDTO> getStudentById(Long id) {
        return null;
    }

    @Override
    public String hi() {
        return "Oops, can't reach student-service /demo/hi endpoint!";
    }
}