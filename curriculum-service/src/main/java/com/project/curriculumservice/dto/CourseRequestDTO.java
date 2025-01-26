package com.project.curriculumservice.dto;

import com.project.curriculumservice.model.Course.CourseStatus;
import com.project.curriculumservice.model.Course.CourseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CourseRequestDTO {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Credit hours are required")
    private Integer creditHrs;

    @NotNull(message = "Course type is required")
    private CourseType type;

    @NotNull(message = "Course status is required")
    private CourseStatus status;

    @NotNull(message = "Department program IDs are required")
    private Set<DepartmentProgramIdDTO> departmentProgramIds;
    private Set<Long> prerequisiteIds;
}
