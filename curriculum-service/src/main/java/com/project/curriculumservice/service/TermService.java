package com.project.curriculumservice.service;

import com.project.curriculumservice.dto.TermRequestDTO;
import com.project.curriculumservice.dto.TermResponseDTO;
import com.project.curriculumservice.exception.ResourceNotFoundException;
import com.project.curriculumservice.model.Term;
import com.project.curriculumservice.repository.TermRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TermService {
    private final TermRepository termRepository;

    public TermResponseDTO createTerm(TermRequestDTO termDTO) {
        validateTermDates(termDTO);
        Term term = mapToTermEntity(termDTO);
        term.setCode(generateTermCode(termDTO.getSeason(), termDTO.getAcademicYear()));
        term = termRepository.save(term);
        return mapToTermResponseDTO(term);
    }

    public List<TermResponseDTO> searchTerms(String codeOrYearQuery) {
        String sanitizedQuery = sanitizeSearchTerm(codeOrYearQuery);
        List<Term> terms = termRepository.searchTerms(sanitizedQuery);
        return terms.stream()
                .map(this::mapToTermResponseDTO)
                .collect(Collectors.toList());
    }

    public TermResponseDTO getTermById(Long termId) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with ID: " + termId));
        return mapToTermResponseDTO(term);
    }

    public TermResponseDTO getTermByCode(String code) {
        Term term = termRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with code: " + code));
        return mapToTermResponseDTO(term);
    }

    public List<TermResponseDTO> getAllTerms() {
        return termRepository.findAll().stream()
                .map(this::mapToTermResponseDTO)
                .collect(Collectors.toList());
    }

    public TermResponseDTO updateTerm(Long termId, TermRequestDTO termDTO) {
        Term existingTerm = termRepository.findById(termId)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with ID: " + termId));

        validateTermDates(termDTO);

        // Generate new code only if season or year has changed
        if (!existingTerm.getSeason().equals(termDTO.getSeason()) ||
                !existingTerm.getAcademicYear().equals(termDTO.getAcademicYear())) {
            existingTerm.setCode(generateTermCode(termDTO.getSeason(), termDTO.getAcademicYear()));
        }

        updateTermEntity(existingTerm, termDTO);
        Term updatedTerm = termRepository.save(existingTerm);
        return mapToTermResponseDTO(updatedTerm);
    }

    public void deleteTerm(Long termId) {
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new ResourceNotFoundException("Term not found with ID: " + termId));
        termRepository.delete(term);
    }

    public String getCurrentTermCode() {
        Term.Season currentSeason = getCurrentSeason();
        Year currentYear = Year.now();
        return generateTermCode(currentSeason, currentYear);
    }

    private String generateTermCode(Term.Season season, Year year) {
        return switch (season) {
            case AUTUMN -> "AUT" + year;
            case WINTER -> "WIN" + year;
            case SPRING -> "SPR" + year;
        };
    }

    private Term.Season getCurrentSeason() {
        int month = LocalDate.now().getMonthValue();

        if (month == 10 || month == 11 || month == 12 || month == 1) {
            return Term.Season.AUTUMN;
        } else if (month >= 2 && month <= 5) {
            return Term.Season.WINTER;
        } else {
            return Term.Season.SPRING;
        }
    }

    private void validateAcademicYear(Year academicYear) {
        Year currentYear = Year.of(LocalDate.now().getYear());

        // Ensure academicYear is not null
        if (academicYear == null) {
            throw new IllegalArgumentException("Academic year cannot be null");
        }

        // Validate academicYear is after or equal to current year
        if (academicYear.isBefore(currentYear)) {
            throw new IllegalArgumentException("Academic year cannot be before the current year");
        }
    }
    private void validateStartDate(LocalDate startDate, String fieldName, LocalDate currentDate) {
        if (startDate.isBefore(currentDate)) {
            throw new IllegalArgumentException(
                    String.format("%s cannot be before current date (%s)", fieldName, currentDate)
            );
        }
    }
    private void validateEndDate(LocalDate startDate, LocalDate endDate, String periodName) {
        if (endDate.isBefore(startDate.plusDays(1))) {
            throw new IllegalArgumentException(
                    String.format("%s end date must be at least one day after %s start date",
                            periodName, periodName.toLowerCase())
            );
        }
    }

    private void validateTermDates(TermRequestDTO termDTO) {
        LocalDate currentDate = LocalDate.now();

        validateAcademicYear(termDTO.getAcademicYear());

        // Validate start dates are not before current date
        validateStartDate(termDTO.getEnrollmentStartDate(), "Enrollment start date", currentDate);
        validateStartDate(termDTO.getAddStartDate(), "Add start date", currentDate);
        validateStartDate(termDTO.getDropStartDate(), "Drop start date", currentDate);

        // Validate end dates are after their corresponding start dates
        validateEndDate(termDTO.getEnrollmentStartDate(), termDTO.getEnrollmentEndDate(), "Enrollment");
        validateEndDate(termDTO.getAddStartDate(), termDTO.getAddEndDate(), "Add");
        validateEndDate(termDTO.getDropStartDate(), termDTO.getDropEndDate(), "Drop");
    }

    private String sanitizeSearchTerm(String codeOrYearQuery) {
        if (codeOrYearQuery == null) {
            return null;
        }
        return codeOrYearQuery.replaceAll("[^a-zA-Z0-9\\s\\-\\.\\,]", "").trim();
    }

    private Term mapToTermEntity(TermRequestDTO termDTO) {
        return Term.builder()
                .academicYear(termDTO.getAcademicYear())
                .season(termDTO.getSeason())
                .enrollmentStartDate(termDTO.getEnrollmentStartDate())
                .enrollmentEndDate(termDTO.getEnrollmentEndDate())
                .addStartDate(termDTO.getAddStartDate())
                .addEndDate(termDTO.getAddEndDate())
                .dropStartDate(termDTO.getDropStartDate())
                .dropEndDate(termDTO.getDropEndDate())
                .build();
    }

    private void updateTermEntity(Term term, TermRequestDTO termDTO) {
        term.setAcademicYear(termDTO.getAcademicYear());
        term.setSeason(termDTO.getSeason());
        term.setEnrollmentStartDate(termDTO.getEnrollmentStartDate());
        term.setEnrollmentEndDate(termDTO.getEnrollmentEndDate());
        term.setAddStartDate(termDTO.getAddStartDate());
        term.setAddEndDate(termDTO.getAddEndDate());
        term.setDropStartDate(termDTO.getDropStartDate());
        term.setDropEndDate(termDTO.getDropEndDate());
    }

    private TermResponseDTO mapToTermResponseDTO(Term term) {
        return TermResponseDTO.builder()
                .termId(term.getTermID())
                .code(term.getCode())
                .academicYear(term.getAcademicYear())
                .season(term.getSeason())
                .enrollmentStartDate(term.getEnrollmentStartDate())
                .enrollmentEndDate(term.getEnrollmentEndDate())
                .addStartDate(term.getAddStartDate())
                .addEndDate(term.getAddEndDate())
                .dropStartDate(term.getDropStartDate())
                .dropEndDate(term.getDropEndDate())
                .build();
    }
}
