package com.project.enrollmentservice.controller;

import com.project.enrollmentservice.dto.CreateEnrollmentRequestDTO;
import com.project.enrollmentservice.dto.EnrollmentRequestResponseDTO;
import com.project.enrollmentservice.dto.UpdateEnrollmentRequestDTO;
import com.project.enrollmentservice.service.SpecialEnrollmentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment-requests")
@RequiredArgsConstructor
public class SpecialEnrollmentRequestController {
    private final SpecialEnrollmentRequestService service;

    @PostMapping
    public ResponseEntity<EnrollmentRequestResponseDTO> createRequest(
            @Valid @RequestBody CreateEnrollmentRequestDTO requestDTO) {
        return ResponseEntity.ok(service.createRequest(requestDTO));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<EnrollmentRequestResponseDTO> getRequest(
            @PathVariable Long requestId) {
        return ResponseEntity.ok(service.getRequest(requestId));
    }


    @GetMapping("/by-student-and-offering")
    public ResponseEntity<EnrollmentRequestResponseDTO> getRequestByStudentIdAndOfferingId(
            @RequestParam Long studentId,
            @RequestParam Long offeringId) {
        var requestOptional = service.getRequestByStudentIdAndOfferingId(studentId, offeringId);

        return requestOptional
                .map(ResponseEntity::ok) // Return 200 OK with the response DTO if found
                .orElse(ResponseEntity.notFound().build()); // Return 404 Not Found if not found
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentRequestResponseDTO>> getAllRequests() {
        return ResponseEntity.ok(service.getAllRequests());
    }

    @PatchMapping("/{requestId}")
    public ResponseEntity<EnrollmentRequestResponseDTO> updateRequestStatus(
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateEnrollmentRequestDTO updateDTO) {
        return ResponseEntity.ok(service.updateRequestStatus(requestId, updateDTO));
    }
}

