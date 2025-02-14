package com.project.studentservice.feignclient.client;

import com.project.studentservice.feignclient.dto.EnrollmentRequestDTO;
import com.project.studentservice.feignclient.dto.EnrollmentResponseDTO;
import com.project.studentservice.feignclient.fallback.EnrollmentServiceFallback;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "enrollment-service", fallback = EnrollmentServiceFallback.class)
public interface EnrollmentServiceClient {

    @GetMapping("/demo/hi")
    String hi();

    @GetMapping("/api/enrollments/{enrollmentId}")
    ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(@PathVariable Long enrollmentId);

    @GetMapping("/api/enrollments/students/{studentId}")
    ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) List<String> status);


    @PutMapping("/api/enrollments/{enrollmentId}")
    ResponseEntity<EnrollmentResponseDTO> updateEnrollment(
            @PathVariable Long enrollmentId,
            @Valid @RequestBody EnrollmentRequestDTO enrollmentDTO);
}