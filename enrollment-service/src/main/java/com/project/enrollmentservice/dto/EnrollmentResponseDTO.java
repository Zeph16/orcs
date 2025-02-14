package com.project.enrollmentservice.dto;

import com.project.enrollmentservice.feignclient.dto.CourseOfferingResponseDTO;
import com.project.enrollmentservice.model.Enrollment;
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
    private Enrollment.EnrollmentType type;
    private Enrollment.EnrollmentStatus status;
    private LocalDate enrollmentDate;
}