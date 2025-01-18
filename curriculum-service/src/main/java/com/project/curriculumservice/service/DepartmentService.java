package com.project.curriculumservice.service;

import com.project.curriculumservice.dto.DepartmentDTO;
import com.project.curriculumservice.exception.ResourceNotFoundException;
import com.project.curriculumservice.model.Department;
import com.project.curriculumservice.model.Program;
import com.project.curriculumservice.repository.DepartmentRepository;
import com.project.curriculumservice.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ProgramRepository programRepository;

    // Create a new department
    public Department createDepartment(DepartmentDTO departmentDTO) {
        if (departmentDTO.getProgramIds() == null || departmentDTO.getProgramIds().isEmpty()) {
            throw new IllegalArgumentException("At least one program must be associated with the department");
        }

        Set<Program> programs = departmentDTO.getProgramIds().stream()
                .map(programId -> programRepository.findById(programId)
                        .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId)))
                .collect(Collectors.toSet());

        Department department = Department.builder()
                .name(departmentDTO.getName())
                .code(departmentDTO.getCode())
                .head(departmentDTO.getHead())
                .programs(programs)
                .build();

        return departmentRepository.save(department);
    }

    // Get a department by ID
    public Department getDepartmentById(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));
    }

    // Get all departments
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    // Update an existing department
    public Department updateDepartment(Long departmentId, DepartmentDTO departmentDTO) {
        Department existingDepartment = getDepartmentById(departmentId);

        existingDepartment.setName(departmentDTO.getName());
        existingDepartment.setCode(departmentDTO.getCode());
        existingDepartment.setHead(departmentDTO.getHead());

        // If the program IDs are updated, fetch and set the programs
        if (departmentDTO.getProgramIds() != null && !departmentDTO.getProgramIds().isEmpty()) {
            Set<Program> programs = departmentDTO.getProgramIds().stream()
                    .map(programId -> programRepository.findById(programId)
                            .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId)))
                    .collect(Collectors.toSet());
            existingDepartment.setPrograms(programs);
        }

        return departmentRepository.save(existingDepartment);
    }

    // Delete a department
    public void deleteDepartment(Long departmentId) {
        Department department = getDepartmentById(departmentId);
        departmentRepository.delete(department);
    }
}
