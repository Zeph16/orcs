package com.project.curriculumservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Year;

@Entity
@Table(name = "terms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Term {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long termID;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private Year academicYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Season season;

    @Column(nullable = false)
    private LocalDate enrollmentStartDate;

    @Column(nullable = false)
    private LocalDate enrollmentEndDate;

    @Column(nullable = false)
    private LocalDate addStartDate;

    @Column(nullable = false)
    private LocalDate addEndDate;

    @Column(nullable = false)
    private LocalDate dropStartDate;

    @Column(nullable = false)
    private LocalDate dropEndDate;
    public enum Season {
        AUTUMN,
        WINTER,
        SPRING
    }
}
