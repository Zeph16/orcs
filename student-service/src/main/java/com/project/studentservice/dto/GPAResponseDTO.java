package com.project.studentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GPAResponseDTO {
    private GPASingle totalGPA;
    private Map<Long, GPASingle> gpaPerTermList;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class GPASingle {
        private Double cumulativeGPA;
        private Double majorGPA;
    }
}