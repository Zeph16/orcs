package com.project.studentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String cardId;
    private String email;
    private String phone;
    private String address;
    private String enrollmentStatus;

    @ManyToOne
    @JoinColumn(name = "batch_id", nullable = false)
    private Batch batch;
}