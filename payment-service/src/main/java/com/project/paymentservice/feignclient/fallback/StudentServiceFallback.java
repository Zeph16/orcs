package com.project.paymentservice.feignclient.fallback;

import com.project.paymentservice.feignclient.client.StudentServiceClient;
import com.project.paymentservice.feignclient.dto.StudentResponseDTO;
import org.springframework.http.ResponseEntity;

public class StudentServiceFallback implements StudentServiceClient {
    @Override
    public String hi() {
        return "Oops, can't reach student-service /demo/hi endpoint!";
    }

    @Override
    public ResponseEntity<StudentResponseDTO> getStudentById(Long id) {
        StudentResponseDTO studentResponseDTO = StudentResponseDTO.builder()
                .id(id)
                .address("Test address")
                .phone("+251-TEST")
                .name("Test Student")
                .email("teststudent@email.com")
                .enrollmentStatus("enrolled")
                .cardId("testid")
                .build();
        return ResponseEntity.ok(studentResponseDTO);
    }


}