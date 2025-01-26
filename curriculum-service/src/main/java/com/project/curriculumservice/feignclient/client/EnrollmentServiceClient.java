package com.project.curriculumservice.feignclient.client;

import com.project.curriculumservice.feignclient.dtos.EnrollmentResponseDTO;
import com.project.curriculumservice.feignclient.fallback.EnrollmentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "enrollment-service", fallback = EnrollmentServiceFallback.class)
public interface EnrollmentServiceClient {
    @GetMapping("/api/enrollments/students/{studentId}")
    ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByStudent(
            @PathVariable int studentId,
            @RequestParam(required = false) List<String> status);

    @GetMapping("/demo/hi")
    String hi();

}
