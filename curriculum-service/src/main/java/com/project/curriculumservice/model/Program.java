package com.project.curriculumservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "programs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Program {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long programID;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Code is required")
    private String code;

    @OneToMany(mappedBy = "program", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<DepartmentProgram> departmentPrograms = new HashSet<>();

    // Convenience method to get associated departments
    @Transient
    public Set<Department> getDepartments() {
        return departmentPrograms.stream()
                .map(DepartmentProgram::getDepartment)
                .collect(Collectors.toSet());
    }
}
