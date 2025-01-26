package com.project.curriculumservice.feignclient.client;

import com.project.curriculumservice.feignclient.dtos.BatchResponseDTO;
import com.project.curriculumservice.feignclient.dtos.StudentResponseDTO;
import com.project.curriculumservice.feignclient.fallback.StudentServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "student-service", fallback = StudentServiceFallback.class)
public interface StudentServiceClient {

    @GetMapping("/demo/hi")
    String hi();
    @GetMapping("/api/batches/id/{id}")
    ResponseEntity<BatchResponseDTO> getBatchById(@PathVariable Long id);
    @GetMapping("/api/students/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable int id);
}
