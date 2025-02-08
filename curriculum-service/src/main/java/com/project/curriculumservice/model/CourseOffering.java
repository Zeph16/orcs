package com.project.curriculumservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "course_offerings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseOffering {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long offeringID;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "batch_id", nullable = false)
    private Long batchID;

    @ManyToOne
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column
    private String instructorName;
}
