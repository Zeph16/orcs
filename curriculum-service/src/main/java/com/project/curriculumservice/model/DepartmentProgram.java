package com.project.curriculumservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "department_program")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentProgram {
    @EmbeddedId
    private DepartmentProgramId id;

    @ManyToOne
    @MapsId("departmentId")
    @JoinColumn(name = "department_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Department department;

    @ManyToOne
    @MapsId("programId")
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToMany(mappedBy = "departmentPrograms")
    private Set<Course> courses = new HashSet<>();

    private int totalRequiredCreditHrs;
}
