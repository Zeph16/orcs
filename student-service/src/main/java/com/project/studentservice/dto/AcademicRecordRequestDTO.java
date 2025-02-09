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
    @NotNull(message = "Student Card ID is required")
    private String studentCardId;

    @NotNull(message = "Course ID is required")
    private Integer courseId;

    @NotNull(message = "Term ID is required")
    private Integer termId;

    @NotNull(message = "Mid Exam score is required")
    private Float midExam;

    @NotNull(message = "Final Exam score is required")
    private Float finalExam;

    @NotNull(message = "Assignment score is required")
    private Float assignment;

    private Float lab; // Nullable (null if course does not have a lab)

    @NotNull(message = "Grade is required")
    private Grade grade;
}