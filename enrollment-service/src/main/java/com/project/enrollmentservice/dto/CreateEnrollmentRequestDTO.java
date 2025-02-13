package com.project.enrollmentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import com.project.enrollmentservice.model.SpecialEnrollmentRequest.RequestType;

@Data
public class CreateEnrollmentRequestDTO {
    @NotNull
    private Long studentID;

    @NotNull
    private Long offeringID;

    @NotNull
    private RequestType requestType;

    @NotNull
    @Size(min = 10, max = 1000)
    private String justification;
}