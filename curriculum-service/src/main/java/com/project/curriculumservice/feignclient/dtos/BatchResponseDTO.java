package com.project.curriculumservice.feignclient.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchResponseDTO implements Serializable {
    private Long id;
    private Long departmentId;
    private Long programId;
    private String code;
    private Character section;
    private Double creditCost;
    private LocalDate expectedGradDate;
    private LocalDate creationDate;
}