package com.project.curriculumservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrerequisiteDTO {
    private Long courseId;
    private String title;
    private String code;
}