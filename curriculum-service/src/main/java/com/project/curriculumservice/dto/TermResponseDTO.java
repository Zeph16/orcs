package com.project.curriculumservice.dto;

import com.project.curriculumservice.model.Term;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.Year;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TermResponseDTO implements Serializable {
    private Long termId;
    private String code;
    private Year academicYear;
    private Term.Season season;
    private LocalDate enrollmentStartDate;
    private LocalDate enrollmentEndDate;
    private LocalDate addStartDate;
    private LocalDate addEndDate;
    private LocalDate dropStartDate;
    private LocalDate dropEndDate;
}