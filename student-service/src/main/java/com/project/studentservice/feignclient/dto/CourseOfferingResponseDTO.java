package com.project.studentservice.feignclient.dto;


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
    private Integer capacity;
    private Integer availableSeats;
    private String instructorName;
}