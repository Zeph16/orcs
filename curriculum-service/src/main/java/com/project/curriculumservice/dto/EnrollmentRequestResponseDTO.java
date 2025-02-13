package com.project.curriculumservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EnrollmentRequestResponseDTO {
    private Long requestID;
    private StudentResponseDTO student;
    private CourseOfferingResponseDTO courseOffering;
    private RequestType requestType;
    private String justification;
    private ApprovalStatus approvalStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum RequestType {
        ADD, REGISTRATION
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }
}
