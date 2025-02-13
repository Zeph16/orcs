package com.project.enrollmentservice.feignclient.client;

import com.project.enrollmentservice.feignclient.dto.StudentResponseDTO;
import com.project.enrollmentservice.feignclient.fallback.StudentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "student-service", fallback = StudentServiceFallback.class)
public interface StudentServiceClient {
    @GetMapping("/api/students/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable int id);
    @GetMapping("/demo/hi")
    String hi();
}