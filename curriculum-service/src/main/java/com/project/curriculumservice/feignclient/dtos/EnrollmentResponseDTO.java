package com.project.curriculumservice.feignclient.dtos;

import com.project.curriculumservice.dto.CourseOfferingResponseDTO;
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
    public enum EnrollmentType {
        REGISTRATION, ADD
    }

    public enum EnrollmentStatus {
        ENROLLED, COMPLETED, NOT_COMPLETED // NOT_COMPLETED represents DROPPED, RC, and RA
    }
}
