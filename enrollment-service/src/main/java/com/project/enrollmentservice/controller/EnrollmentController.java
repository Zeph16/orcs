package com.project.enrollmentservice.controller;

import com.project.enrollmentservice.dto.EnrollmentRequestDTO;
import com.project.enrollmentservice.dto.EnrollmentResponseDTO;
import com.project.enrollmentservice.feignclient.dto.CourseOfferingResponseDTO;
import com.project.enrollmentservice.feignclient.dto.CourseResponseDTO;
import com.project.enrollmentservice.model.Enrollment;
import com.project.enrollmentservice.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> createEnrollment(@Valid @RequestBody EnrollmentRequestDTO enrollmentDTO) {
        return new ResponseEntity<>(enrollmentService.createEnrollment(enrollmentDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{enrollmentId}")
    public ResponseEntity<EnrollmentResponseDTO> getEnrollmentById(@PathVariable Long enrollmentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentById(enrollmentId));
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentResponseDTO>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @PutMapping("/{enrollmentId}")
    public ResponseEntity<EnrollmentResponseDTO> updateEnrollment(
            @PathVariable Long enrollmentId,
            @Valid @RequestBody EnrollmentRequestDTO enrollmentDTO) {
        return ResponseEntity.ok(enrollmentService.updateEnrollment(enrollmentId, enrollmentDTO));
    }

    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> deleteEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.deleteEnrollment(enrollmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByStudent(
            @PathVariable int studentId,
            @RequestParam(required = false) List<String> status) {

        if (status == null || status.isEmpty()) {
            // Return all enrollments for the student
            return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudent(studentId));
        } else {
            // Return filtered enrollments based on the provided statuses
            List<Enrollment.EnrollmentStatus> statuses = status.stream()
                    .map(Enrollment.EnrollmentStatus::valueOf)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(enrollmentService.getEnrollmentsByStudentAndStatus(studentId, statuses));
        }
    }

    @GetMapping("/offering/{offeringId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByOffering(@PathVariable Long offeringId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByOffering(offeringId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourse(courseId));
    }
}