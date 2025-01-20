package com.project.curriculumservice.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramResponseDTO {
    private Long programId;
    private String name;
    private String code;
    private List<DepartmentResponseDTO> departments;
}
