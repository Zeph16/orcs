package com.project.studentservice.feignclient.client;

import com.project.studentservice.feignclient.dto.EnrollmentRequestDTO;
import com.project.studentservice.feignclient.dto.EnrollmentResponseDTO;
import com.project.studentservice.feignclient.fallback.EnrollmentServiceFallback;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "enrollment-service", fallback = EnrollmentServiceFallback.class)
public interface EnrollmentServiceClient {

    @GetMapping("/demo/hi")
    String hi();

    @GetMapping("/{enrollmentId}")
    ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(@PathVariable Long enrollmentId);


    @PutMapping("/{enrollmentId}")
    ResponseEntity<EnrollmentResponseDTO> updateEnrollment(
            @PathVariable Long enrollmentId,
            @Valid @RequestBody EnrollmentRequestDTO enrollmentDTO);
}