package com.project.studentservice.feignclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRequestDTO {
    private Long enrollmentID;
    private Long studentID;
    private Long offeringID;
    private EnrollmentType type;
    private EnrollmentStatus status;
}
