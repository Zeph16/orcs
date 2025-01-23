package com.project.studentservice.feignclient.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponseDTO {
    private Long departmentId;
    private String name;
    private String code;
    private String head;
    private List<ProgramResponseDTO> programs;
}