package com.project.curriculumservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "departments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentID;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    private String code;

    private String head;

    @OneToMany(mappedBy = "department", cascade = CascadeType.MERGE, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<DepartmentProgram> departmentPrograms = new HashSet<>();

}

