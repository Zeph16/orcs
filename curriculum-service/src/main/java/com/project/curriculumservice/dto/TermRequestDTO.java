package com.project.curriculumservice.dto;

import com.project.curriculumservice.model.Term;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.Year;

@Data
@Builder
public class TermRequestDTO {
    private Year academicYear;
    private Term.Season season;
    private LocalDate enrollmentStartDate;
    private LocalDate enrollmentEndDate;
    private LocalDate addStartDate;
    private LocalDate addEndDate;
    private LocalDate dropStartDate;
    private LocalDate dropEndDate;
}
