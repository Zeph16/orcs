package com.project.enrollmentservice.service;

import com.project.enrollmentservice.dto.AddCourseRecommendationResponse;
import com.project.enrollmentservice.dto.CreateEnrollmentRequestDTO;
import com.project.enrollmentservice.dto.EnrollmentRequestResponseDTO;
import com.project.enrollmentservice.dto.UpdateEnrollmentRequestDTO;
import com.project.enrollmentservice.exception.ResourceNotFoundException;
import com.project.enrollmentservice.feignclient.client.CurriculumServiceClient;
import com.project.enrollmentservice.feignclient.client.StudentServiceClient;
import com.project.enrollmentservice.feignclient.dto.CourseOfferingResponseDTO;
import com.project.enrollmentservice.feignclient.dto.StudentResponseDTO;
import com.project.enrollmentservice.model.SpecialEnrollmentRequest;
import com.project.enrollmentservice.repository.SpecialEnrollmentRequestRepository;
import com.project.enrollmentservice.util.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialEnrollmentRequestService {
    private final SpecialEnrollmentRequestRepository repository;
    private final StudentServiceClient studentServiceClient;
    private final CurriculumServiceClient curriculumServiceClient;
    private final EventPublisher eventPublisher;

    @Transactional
    public EnrollmentRequestResponseDTO createRequest(CreateEnrollmentRequestDTO requestDTO) {
        // Validate student and course offering exist
        var studentResponse = studentServiceClient.getStudentById(requestDTO.getStudentID())
                .getBody();
        var courseOfferingResponse = curriculumServiceClient.getCourseOfferingById(requestDTO.getOfferingID())
                .getBody();

        if (studentResponse == null || courseOfferingResponse == null) {
            throw new ResourceNotFoundException("Student or Course Offering not found");
        }

        var request = SpecialEnrollmentRequest.builder()
                .studentID(requestDTO.getStudentID())
                .offeringID(requestDTO.getOfferingID())
                .requestType(requestDTO.getRequestType())
                .justification(requestDTO.getJustification())
                .approvalStatus(SpecialEnrollmentRequest.ApprovalStatus.PENDING)
                .build();

        var savedRequest = repository.save(request);
        return buildResponseDTO(savedRequest, studentResponse, courseOfferingResponse);
    }

    @Transactional(readOnly = true)
    public Optional<EnrollmentRequestResponseDTO> getRequestByStudentIdAndOfferingId(Long studentId, Long offeringId) {
        // Find the request by studentId and offeringId
        var requestOptional = repository.findByStudentIDAndOfferingID(studentId, offeringId);

        if (requestOptional.isEmpty()) {
            return Optional.empty(); // Return empty if no request is found
        }

        var request = requestOptional.get();

        // Fetch student and course offering details
        var studentResponse = studentServiceClient.getStudentById(request.getStudentID())
                .getBody();
        var courseOfferingResponse = curriculumServiceClient.getCourseOfferingById(request.getOfferingID())
                .getBody();

        // Build and return the response DTO
        return Optional.of(buildResponseDTO(request, studentResponse, courseOfferingResponse));
    }

    @Transactional(readOnly = true)
    public EnrollmentRequestResponseDTO getRequest(Long requestId) {
        var request = repository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        var studentResponse = studentServiceClient.getStudentById(request.getStudentID())
                .getBody();
        var courseOfferingResponse = curriculumServiceClient.getCourseOfferingById(request.getOfferingID())
                .getBody();

        return buildResponseDTO(request, studentResponse, courseOfferingResponse);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentRequestResponseDTO> getAllRequests() {
        return repository.findAll().stream()
                .map(request -> {
                    var studentResponse = studentServiceClient.getStudentById(request.getStudentID())
                            .getBody();
                    var courseOfferingResponse = curriculumServiceClient.getCourseOfferingById(request.getOfferingID())
                            .getBody();
                    return buildResponseDTO(request, studentResponse, courseOfferingResponse);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentRequestResponseDTO updateRequestStatus(Long requestId, UpdateEnrollmentRequestDTO updateDTO) {
        var request = repository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        request.setApprovalStatus(updateDTO.getApprovalStatus());
        var updatedRequest = repository.save(request);
        var studentResponse = studentServiceClient.getStudentById(request.getStudentID())
                .getBody();
        var courseOfferingResponse = curriculumServiceClient.getCourseOfferingById(request.getOfferingID())
                .getBody();

        EnrollmentRequestResponseDTO res = buildResponseDTO(updatedRequest, studentResponse, courseOfferingResponse);

        if (updateDTO.getApprovalStatus().equals(SpecialEnrollmentRequest.ApprovalStatus.APPROVED)) {
            eventPublisher.publishEnrollmentRequestApproved(res);
        } else {
            eventPublisher.publishEnrollmentRequestDenied(res);
        }

        return res;
    }

    private EnrollmentRequestResponseDTO buildResponseDTO(
            SpecialEnrollmentRequest request,
            StudentResponseDTO student,
            CourseOfferingResponseDTO courseOffering) {
        return EnrollmentRequestResponseDTO.builder()
                .requestID(request.getRequestID())
                .student(student)
                .courseOffering(courseOffering)
                .requestType(request.getRequestType())
                .justification(request.getJustification())
                .approvalStatus(request.getApprovalStatus())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
