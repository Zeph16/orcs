package com.project.enrollmentservice.dto;

import com.project.enrollmentservice.feignclient.dto.StudentResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibleStudentsDTO {
    private StudentResponseDTO student;
    private CourseDetails coursesTaking;
    private CourseDetails coursesRecommended;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CourseDetails {
        private int major;
        private int common;
        private int elective;
    }
}
