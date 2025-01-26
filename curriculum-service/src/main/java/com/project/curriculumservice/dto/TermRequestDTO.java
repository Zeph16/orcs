package com.project.curriculumservice.dto;

import com.project.curriculumservice.model.Term;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.Year;

@Data
@Builder
public class TermRequestDTO {
    @NotNull(message = "Academic year is required")
    private Year academicYear;

    @NotNull(message = "Season is required")
    private Term.Season season;

    @NotNull(message = "Enrollment start date is required")
    @FutureOrPresent(message = "Enrollment start date must be today or in the future")
    private LocalDate enrollmentStartDate;

    @NotNull(message = "Enrollment end date is required")
    @FutureOrPresent(message = "Enrollment end date must be today or in the future")
    private LocalDate enrollmentEndDate;

    @NotNull(message = "Add start date is required")
    @FutureOrPresent(message = "Add start date must be today or in the future")
    private LocalDate addStartDate;

    @NotNull(message = "Add end date is required")
    @FutureOrPresent(message = "Add end date must be today or in the future")
    private LocalDate addEndDate;

    @NotNull(message = "Drop start date is required")
    @FutureOrPresent(message = "Drop start date must be today or in the future")
    private LocalDate dropStartDate;

    @NotNull(message = "Drop end date is required")
    @FutureOrPresent(message = "Drop end date must be today or in the future")
    private LocalDate dropEndDate;
}
