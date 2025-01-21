package com.project.curriculumservice.service;

import com.project.curriculumservice.exception.ResourceNotFoundException;
import com.project.curriculumservice.model.Department;
import com.project.curriculumservice.model.DepartmentProgram;
import com.project.curriculumservice.model.DepartmentProgramId;
import com.project.curriculumservice.model.Program;
import com.project.curriculumservice.repository.DepartmentProgramRepository;
import com.project.curriculumservice.repository.DepartmentRepository;
import com.project.curriculumservice.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentProgramService {
    private final DepartmentRepository departmentRepository;
    private final ProgramRepository programRepository;
    private final DepartmentProgramRepository departmentProgramRepository;

    public DepartmentProgram getDepartmentProgramById(Long departmentId, Long programId) {
        return departmentProgramRepository.findByDepartment_DepartmentIDAndProgram_ProgramID(departmentId, programId)
                .orElseThrow(() -> new ResourceNotFoundException("DepartmentProgram not found for Department ID: " + departmentId + " and Program ID: " + programId));
    }
    public List<Program> getPrograms(Long departmentId) {
        return departmentProgramRepository.findByDepartment_DepartmentID(departmentId)
                .stream()
                .map(DepartmentProgram::getProgram)
                .toList();
    }

    public List<Department> getDepartments(Long programId) {
        return departmentProgramRepository.findByProgram_ProgramID(programId)
                .stream()
                .map(DepartmentProgram::getDepartment)
                .toList();
    }

    public DepartmentProgram createDepartmentProgram(Long departmentId, Long programId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId));

        DepartmentProgramId id = new DepartmentProgramId(departmentId, programId);

        return departmentProgramRepository.save(DepartmentProgram.builder()
                .id(id)
                .department(department)
                .program(program)
                .build());
    }
    public DepartmentProgram createDepartmentProgram(Department department, Long programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId));

        DepartmentProgramId id = new DepartmentProgramId(department.getDepartmentID(), programId);

        return departmentProgramRepository.save(DepartmentProgram.builder()
                .id(id)
                .department(department)
                .program(program)
                .build());
    }
}
