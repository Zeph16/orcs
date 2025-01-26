package com.project.studentservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Entity
@Table(name = "batches", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"creationDate", "section"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Batch {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private Long departmentId;
    @Column(nullable = false)
    private Long programId;

    @Column(unique = true)
    private String code;
    private Character section;
    @Column(nullable = false)
    private double creditCost;
    @Column(nullable = false)
    private LocalDate expectedGradDate;

    @CreatedDate
    private LocalDate creationDate;
}