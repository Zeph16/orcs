package com.project.curriculumservice.feignclient.fallback;

import com.project.curriculumservice.feignclient.dtos.BatchResponseDTO;
import com.project.curriculumservice.feignclient.client.StudentServiceClient;
import com.project.curriculumservice.feignclient.dtos.StudentResponseDTO;
import org.springframework.http.ResponseEntity;

public class StudentServiceFallback implements StudentServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach student-service /demo/hi endpoint!";
    }
    @Override
    public ResponseEntity<BatchResponseDTO> getBatchById(Long id) {
        // Provide a default or fallback response
        BatchResponseDTO fallbackResponse = new BatchResponseDTO();
        fallbackResponse.setId(id);
        fallbackResponse.setCode("Code Unavailable");
        return ResponseEntity.ok(fallbackResponse);
    }

    @Override
    public ResponseEntity<StudentResponseDTO> getStudentById(Long id) {
        // Provide a default or fallback response
        StudentResponseDTO fallbackResponse = new StudentResponseDTO();
        fallbackResponse.setId(id);
        fallbackResponse.setName("ERROR: THIS IS FALLBACK RESPONSE");
        return ResponseEntity.ok(fallbackResponse);
    }
}
