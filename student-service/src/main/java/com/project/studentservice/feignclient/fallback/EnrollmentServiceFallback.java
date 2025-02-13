package com.project.studentservice.feignclient.fallback;

import com.project.studentservice.feignclient.client.EnrollmentServiceClient;
import com.project.studentservice.feignclient.dto.EnrollmentRequestDTO;
import com.project.studentservice.feignclient.dto.EnrollmentResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class EnrollmentServiceFallback implements EnrollmentServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach enrollment-service /demo/hi endpoint!";
    }

    @Override
    public ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(Long enrollmentId) {
        return null;
    }

    @Override
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByStudent(Long studentId, List<String> status) {
        return null;
    }

    @Override
    public ResponseEntity<EnrollmentResponseDTO> updateEnrollment(Long enrollmentId, EnrollmentRequestDTO enrollmentDTO) {
        return null;
    }
}