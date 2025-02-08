package com.project.curriculumservice.dto;

import com.project.curriculumservice.feignclient.dtos.BatchResponseDTO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseOfferingResponseDTO {
    private Long offeringID;
    private CourseResponseDTO course;
    private BatchResponseDTO batch;
    private TermResponseDTO term;
    private Integer capacity;
    private Integer availableSeats;
    private String instructorName;
}