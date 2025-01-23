package com.project.studentservice.dto;

import com.project.studentservice.model.Grade;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcademicRecordRequestDTO {
    @NotNull(message = "Student ID is required")
    private Integer studentId;

    @NotNull(message = "Course ID is required")
    private Integer courseId;

    @NotNull(message = "Term ID is required")
    private Integer termId;

    @NotNull(message = "Grade is required")
    private Grade grade;
}