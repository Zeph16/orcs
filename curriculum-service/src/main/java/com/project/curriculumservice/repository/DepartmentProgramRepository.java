package com.project.curriculumservice.repository;

import com.project.curriculumservice.model.DepartmentProgram;
import com.project.curriculumservice.model.DepartmentProgramId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentProgramRepository extends JpaRepository<DepartmentProgram, DepartmentProgramId> {
    List<DepartmentProgram> findByDepartment_DepartmentID(Long departmentId);
    List<DepartmentProgram> findByProgram_ProgramID(Long programId);

}
