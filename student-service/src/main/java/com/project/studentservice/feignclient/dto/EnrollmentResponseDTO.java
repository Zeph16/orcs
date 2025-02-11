package com.project.studentservice.feignclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponseDTO {
    private Long enrollmentID;
    private Long studentID;
    private CourseOfferingResponseDTO courseOffering;
    private EnrollmentType type;
    private EnrollmentStatus status;
    private LocalDate enrollmentDate;
}