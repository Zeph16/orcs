package com.project.curriculumservice.feignclient.fallback;

import com.project.curriculumservice.dto.CourseOfferingResponseDTO;
import com.project.curriculumservice.dto.EnrollmentRequestResponseDTO;
import com.project.curriculumservice.feignclient.client.EnrollmentServiceClient;
import com.project.curriculumservice.feignclient.dtos.EnrollmentResponseDTO;
import com.project.curriculumservice.feignclient.dtos.EnrollmentStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public class EnrollmentServiceFallback implements EnrollmentServiceClient {
    @Override
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByStudent(Long studentId, List<String> status) {
        return null;
    }

    @Override
    public ResponseEntity<EnrollmentRequestResponseDTO> getRequestByStudentIdAndOfferingId(Long studentId, Long offeringId) {
        return ResponseEntity.ok(null);
    }

    @Override
    public String hi() {
        return "Oops, can't reach enrollment-service /demo/hi endpoint!";
    }
}
