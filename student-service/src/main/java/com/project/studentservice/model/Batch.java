package com.project.studentservice.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Batch {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;
    private Long departmentId;
    private Long programId;

    @Column(unique = true)
    private String code;

    private char section;
    private double creditCost;
    private LocalDate expectedGradDate;

    @CreatedDate
    private LocalDate creationDate;
}