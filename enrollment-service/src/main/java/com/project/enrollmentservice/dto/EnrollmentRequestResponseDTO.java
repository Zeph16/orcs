package com.project.enrollmentservice.dto;

import com.project.enrollmentservice.feignclient.dto.CourseOfferingResponseDTO;
import com.project.enrollmentservice.feignclient.dto.StudentResponseDTO;
import com.project.enrollmentservice.model.SpecialEnrollmentRequest;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class EnrollmentRequestResponseDTO {
    private Long requestID;
    private StudentResponseDTO student;
    private CourseOfferingResponseDTO courseOffering;
    private SpecialEnrollmentRequest.RequestType requestType;
    private String justification;
    private SpecialEnrollmentRequest.ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

