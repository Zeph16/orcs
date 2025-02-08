package com.project.curriculumservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseOfferingRequestDTO {
    @NotBlank(message = "Course ID is required")
    private Long courseId;
    @NotBlank(message = "Batch ID is required")
    private Long batchId;
    @NotBlank(message = "Term ID is required")
    private Long termId;
    @NotBlank(message = "Capacity is required")
    private Integer capacity;
    private String instructorName;
}