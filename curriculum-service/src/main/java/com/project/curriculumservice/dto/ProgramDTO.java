package com.project.curriculumservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramDTO {
    private Long programId;

    @NotEmpty(message = "Name is required")
    private String name;
    @NotBlank(message = "Code is required")
    private String code;
}