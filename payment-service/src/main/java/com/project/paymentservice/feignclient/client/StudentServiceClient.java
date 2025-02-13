package com.project.paymentservice.feignclient.client;

import com.project.paymentservice.feignclient.dto.StudentResponseDTO;
import com.project.paymentservice.feignclient.fallback.StudentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "student-service", fallback = StudentServiceFallback.class)
public interface StudentServiceClient {
    @GetMapping("/demo/hi")
    String hi();

    @GetMapping("/api/students/{id}")
    ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id);
}