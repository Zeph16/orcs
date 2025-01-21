package com.project.curriculumservice.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentProgramIdDTO {
    private Long departmentId;
    private Long programId;
}
