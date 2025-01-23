package com.project.studentservice.feignclient.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseResponseDTO {
    private Long courseId;
    private String title;
    private String code;
    private Integer creditHrs;
    private String type;
    private String status;
}