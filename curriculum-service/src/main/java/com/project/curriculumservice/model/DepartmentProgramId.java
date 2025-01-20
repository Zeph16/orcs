package com.project.curriculumservice.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentProgramId implements java.io.Serializable {
    private Long departmentId;
    private Long programId;
}
