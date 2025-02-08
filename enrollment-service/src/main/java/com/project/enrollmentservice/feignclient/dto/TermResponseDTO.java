package com.project.enrollmentservice.feignclient.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.Year;

@Data
@Builder
public class TermResponseDTO {
    private Long termId;
    private String code;
}