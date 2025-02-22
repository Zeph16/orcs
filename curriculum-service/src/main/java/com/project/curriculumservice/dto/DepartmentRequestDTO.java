package com.project.curriculumservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequestDTO {

    private Long departmentId;

    @NotEmpty(message = "Name is required")
    private String name;

    @NotEmpty(message = "Code is required")
    private String code;

    private String head;

    @NotEmpty(message = "Total required credit hours is required")
    private Map<Long, Integer> programCredits; // Map of programId to required credit hours
}



