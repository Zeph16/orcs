package com.project.curriculumservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProgramWithCreditsDTO {
    private Long programId;
    private String name;
    private String code;
    private int totalRequiredCreditHrs;
}
