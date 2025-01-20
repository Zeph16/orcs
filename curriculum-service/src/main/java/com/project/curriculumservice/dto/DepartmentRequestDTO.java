package com.project.curriculumservice.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

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

    @NotEmpty(message = "At least one program is required")
    private Set<Long> programIds; // List of Program IDs to associate
}



