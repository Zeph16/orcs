package com.project.enrollmentservice.feignclient.dto;


import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CourseResponseDTO {
    private Long courseId;
    private String title;
    private String code;
    private Integer creditHrs;
    private CourseType type;
    public enum CourseType {
        MAJOR, ELECTIVE, COMMON
    }
}