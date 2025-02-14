package com.project.enrollmentservice.dto;

import com.project.enrollmentservice.feignclient.dto.CourseResponseDTO;
import com.project.enrollmentservice.feignclient.dto.StudentResponseDTO;
import com.project.enrollmentservice.feignclient.dto.TermResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCourseRecommendationResponse {
    private Long recommendationID;
    private StudentResponseDTO student;
    private TermResponseDTO term;
    private CourseResponseDTO course;
}
