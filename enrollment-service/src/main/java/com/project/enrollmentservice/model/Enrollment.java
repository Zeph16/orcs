package com.project.enrollmentservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentID;

    @NotNull(message = "Student ID is required")
    private Long studentID;

    @NotNull(message = "Offering ID is required")
    private Long offeringID;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Enrollment type is required")
    private EnrollmentType type;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Enrollment status is required")
    private EnrollmentStatus status;

    @NotNull(message = "Enrollment date is required")
    private LocalDate enrollmentDate;

    public enum EnrollmentType {
        REGISTRATION, ADD
    }

    public enum EnrollmentStatus {
        ENROLLED, COMPLETED, NOT_COMPLETED // NOT_COMPLETED represents DROPPED, RC, and RA
    }
}

