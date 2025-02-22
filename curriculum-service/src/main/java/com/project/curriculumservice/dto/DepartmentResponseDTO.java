package com.project.curriculumservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponseDTO {
    private Long departmentId;
    private String name;
    private String code;
    private String head;
    private List<ProgramWithCreditsDTO> programs;
}
