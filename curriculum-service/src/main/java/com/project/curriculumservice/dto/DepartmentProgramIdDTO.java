package com.project.curriculumservice.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentProgramIdDTO {
    @NotNull(message = "Department ID is required")
    private Long departmentId;

    @NotNull(message = "Program ID is required")
    private Long programId;
}
