package com.project.curriculumservice.dto;

import com.project.curriculumservice.model.Course.CourseStatus;
import com.project.curriculumservice.model.Course.CourseType;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CourseRequestDTO {
    private String title;
    private String code;
    private Integer creditHrs;
    private CourseType type;
    private CourseStatus status;
    private Set<DepartmentProgramIdDTO> departmentProgramIds;
    private Set<Long> prerequisiteIds;
}
