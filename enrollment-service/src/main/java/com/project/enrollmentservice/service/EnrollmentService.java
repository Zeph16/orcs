package com.project.enrollmentservice.service;

import com.project.enrollmentservice.dto.EnrollmentRequestDTO;
import com.project.enrollmentservice.dto.EnrollmentResponseDTO;
import com.project.enrollmentservice.exception.ResourceNotFoundException;
import com.project.enrollmentservice.feignclient.client.CurriculumServiceClient;
import com.project.enrollmentservice.feignclient.dto.CourseOfferingResponseDTO;
import com.project.enrollmentservice.feignclient.dto.CourseResponseDTO;
import com.project.enrollmentservice.model.Enrollment;
import com.project.enrollmentservice.repository.EnrollmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CurriculumServiceClient curriculumServiceClient;

    public EnrollmentResponseDTO createEnrollment(EnrollmentRequestDTO enrollmentDTO) {
        ResponseEntity<CourseOfferingResponseDTO> response = curriculumServiceClient.getCourseOfferingById(enrollmentDTO.getOfferingID());
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new ResourceNotFoundException("Course offering not found with ID: " + enrollmentDTO.getOfferingID());
        }
        Enrollment enrollment = mapToEnrollmentEntity(enrollmentDTO);
        enrollment = enrollmentRepository.save(enrollment);
        return mapToEnrollmentResponseDTO(enrollment);
    }

    public EnrollmentResponseDTO getEnrollmentById(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with ID: " + enrollmentId));
        return mapToEnrollmentResponseDTO(enrollment);
    }

    public List<EnrollmentResponseDTO> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(this::mapToEnrollmentResponseDTO)
                .collect(Collectors.toList());
    }

    public EnrollmentResponseDTO updateEnrollment(Long enrollmentId, EnrollmentRequestDTO enrollmentDTO) {
        Enrollment existingEnrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with ID: " + enrollmentId));

        existingEnrollment.setStudentID(enrollmentDTO.getStudentID());
        existingEnrollment.setOfferingID(enrollmentDTO.getOfferingID());
        existingEnrollment.setType(enrollmentDTO.getType());
        existingEnrollment.setStatus(enrollmentDTO.getStatus());

        Enrollment updatedEnrollment = enrollmentRepository.save(existingEnrollment);
        return mapToEnrollmentResponseDTO(updatedEnrollment);
    }

    public void deleteEnrollment(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with ID: " + enrollmentId));
        enrollmentRepository.delete(enrollment);
    }

    // Additional methods for custom queries
    public List<EnrollmentResponseDTO> getEnrollmentsByStudentAndStatus(int studentId, List<Enrollment.EnrollmentStatus> statuses) {
        // Fetch enrollments with the specified statuses
        return enrollmentRepository.findByStudentIDAndStatusIn(studentId, statuses).stream()
                .map(this::mapToEnrollmentResponseDTO)
                .filter(Objects::nonNull) // Remove any null results
                .collect(Collectors.toList());
    }

    public List<EnrollmentResponseDTO> getEnrollmentsByStudent(int studentId) {
        return enrollmentRepository.findByStudentID(studentId).stream()
                .map(this::mapToEnrollmentResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EnrollmentResponseDTO> getEnrollmentsByOffering(Long offeringId) {
        return enrollmentRepository.findByOfferingID(offeringId).stream()
                .map(this::mapToEnrollmentResponseDTO)
                .collect(Collectors.toList());
    }

    public List<EnrollmentResponseDTO> getEnrollmentsByCourse(Long courseId) {
        List<CourseOfferingResponseDTO> offerings = curriculumServiceClient.getCourseOfferingsByCourseId(courseId).getBody();

        if (offerings == null || offerings.isEmpty()) {
            return Collections.emptyList();
        }

        return offerings.stream()
                .flatMap(offering -> enrollmentRepository.findByOfferingID(offering.getOfferingID()).stream())
                .map(this::mapToEnrollmentResponseDTO)
                .collect(Collectors.toList());
    }


    private Enrollment mapToEnrollmentEntity(EnrollmentRequestDTO enrollmentDTO) {
        return Enrollment.builder()
                .studentID(enrollmentDTO.getStudentID())
                .offeringID(enrollmentDTO.getOfferingID())
                .type(enrollmentDTO.getType())
                .status(enrollmentDTO.getStatus())
                .enrollmentDate(LocalDate.now())
                .build();
    }

    private EnrollmentResponseDTO mapToEnrollmentResponseDTO(Enrollment enrollment) {
        return EnrollmentResponseDTO.builder()
                .enrollmentID(enrollment.getEnrollmentID())
                .studentID(enrollment.getStudentID())
                .courseOffering(curriculumServiceClient.getCourseOfferingById(enrollment.getOfferingID()).getBody())
                .type(enrollment.getType())
                .status(enrollment.getStatus())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .build();
    }
}