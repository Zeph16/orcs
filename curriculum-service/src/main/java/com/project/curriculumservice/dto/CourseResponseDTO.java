package com.project.curriculumservice.dto;

import com.project.curriculumservice.model.Course.CourseStatus;
import com.project.curriculumservice.model.Course.CourseType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseResponseDTO {
    private Long courseId;
    private String title;
    private String code;
    private Integer creditHrs;
    private CourseType type;
    private CourseStatus status;
    private List<DepartmentProgramResponseDTO> departmentPrograms;
    private List<PrerequisiteDTO> prerequisites;
}