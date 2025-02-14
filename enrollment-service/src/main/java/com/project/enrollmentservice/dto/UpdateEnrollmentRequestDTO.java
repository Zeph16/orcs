package com.project.enrollmentservice.dto;

import com.project.enrollmentservice.model.SpecialEnrollmentRequest;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateEnrollmentRequestDTO {
    @NotNull
    private SpecialEnrollmentRequest.ApprovalStatus approvalStatus;
}