package com.project.curriculumservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseID;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Code is required")
    private String code;

    @NotNull(message = "Credit hours are required")
    private Integer creditHrs;

    @NotNull(message = "Type is required")
    @Enumerated(EnumType.STRING)
    private CourseType type;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "course_department_program",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = {
                    @JoinColumn(name = "department_id"),
                    @JoinColumn(name = "program_id")
            }
    )
    private Set<DepartmentProgram> departmentPrograms = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "course_prerequisites",
            joinColumns = @JoinColumn(name = "course_id"),
            inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    private Set<Course> prerequisites = new HashSet<>();

    public enum CourseStatus {
        OPEN, CLOSED
    }

    public enum CourseType {
        MAJOR, ELECTIVE, COMMON
    }
}
