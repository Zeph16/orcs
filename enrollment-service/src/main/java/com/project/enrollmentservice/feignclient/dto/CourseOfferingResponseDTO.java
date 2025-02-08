package com.project.enrollmentservice.feignclient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseOfferingResponseDTO {
    private Long offeringID;
    private CourseResponseDTO course;
    private BatchResponseDTO batch;
    private TermResponseDTO term;
    private Integer capacity;
    private Integer availableSeats;
    private String instructorName;
}