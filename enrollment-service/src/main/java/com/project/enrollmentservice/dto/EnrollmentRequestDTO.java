package com.project.enrollmentservice.dto;

import com.project.enrollmentservice.model.Enrollment;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRequestDTO {
    private Long enrollmentID;

    @NotNull(message = "Student ID is required")
    private Integer studentID;

    @NotNull(message = "Offering ID is required")
    private Long offeringID;

    @NotNull(message = "Enrollment type is required")
    private Enrollment.EnrollmentType type;

    @NotNull(message = "Enrollment status is required")
    private Enrollment.EnrollmentStatus status;
}
