package com.project.studentservice.feignclient.dto;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CourseResponseDTO {
    private Long courseId;
    private String title;
    private String code;
    private Integer creditHrs;
    private CourseType type;
    private String description;
    private List<String> courseObjectives;
    private List<String> courseContent;
}