package com.project.curriculumservice.dto;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class DepartmentProgramResponseDTO {
    private Long departmentId;
    private String departmentName;
    private Long programId;
    private String programName;
}

