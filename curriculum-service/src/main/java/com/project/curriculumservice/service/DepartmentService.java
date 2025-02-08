package com.project.curriculumservice.service;

import com.project.curriculumservice.dto.DepartmentRequestDTO;
import com.project.curriculumservice.dto.DepartmentResponseDTO;
import com.project.curriculumservice.dto.ProgramResponseDTO;
import com.project.curriculumservice.exception.ResourceNotFoundException;
import com.project.curriculumservice.model.Department;
import com.project.curriculumservice.model.DepartmentProgram;
import com.project.curriculumservice.model.DepartmentProgramId;
import com.project.curriculumservice.model.Program;
import com.project.curriculumservice.repository.DepartmentProgramRepository;
import com.project.curriculumservice.repository.DepartmentRepository;
import com.project.curriculumservice.repository.ProgramRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentProgramService departmentProgramService;

    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentDTO) {
        if (departmentDTO.getProgramIds() == null || departmentDTO.getProgramIds().isEmpty()) {
            throw new IllegalArgumentException("At least one program must be associated with the department");
        }

        // First, create the department
        Department department = mapToDepartmentEntity(departmentDTO);

        // Save the department first
        department = departmentRepository.save(department);

        // Then create the department-program relationships
        for (Long programId : departmentDTO.getProgramIds()) {
            departmentProgramService.createDepartmentProgram(department, programId);
        }

        return mapToDepartmentResponseDTO(department, departmentProgramService.getPrograms(department.getDepartmentID()));
    }

    // Get a department by ID and return as DepartmentResponseDTO
    public DepartmentResponseDTO getDepartmentById(Long departmentId) {
        // Fetch the department entity
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));

        // Fetch associated programs using departmentProgramRepository
        List<Program> programs = departmentProgramService.getPrograms(departmentId);

        // Map the department and its programs to DepartmentResponseDTO
        return mapToDepartmentResponseDTO(department, programs);
    }

    // Get all departments and return as a list of DepartmentResponseDTO
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(department -> {
                    // Fetch associated programs using departmentProgramRepository
                    List<Program> programs = departmentProgramService.getPrograms(department.getDepartmentID());

                    // Map the department and its programs to DepartmentResponseDTO
                    return mapToDepartmentResponseDTO(department, programs);
                })
                .collect(Collectors.toList());
    }

    // Update an existing department
    public DepartmentResponseDTO updateDepartment(Long departmentId, DepartmentRequestDTO departmentDTO) {
        Department existingDepartment = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));

        // Update basic department information
        existingDepartment.setName(departmentDTO.getName());
        existingDepartment.setCode(departmentDTO.getCode());
        existingDepartment.setHead(departmentDTO.getHead());

        // Handle program associations
        if (departmentDTO.getProgramIds() != null) {
            departmentProgramService.updateDepartmentPrograms(existingDepartment, departmentDTO.getProgramIds());
        }

        Department department = departmentRepository.save(existingDepartment);
        return mapToDepartmentResponseDTO(department, departmentProgramService.getPrograms(departmentId));
    }

    // Delete a department
    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));
        departmentRepository.delete(department);
    }
    private DepartmentResponseDTO mapToDepartmentResponseDTO(Department department, List<Program> programs) {
        // Map programs to the required DTO format
        List<ProgramResponseDTO> programDTOs = programs.stream()
                .map(program -> ProgramResponseDTO.builder()
                        .programId(program.getProgramID())
                        .name(program.getName())
                        .code(program.getCode())
                        .departments(new ArrayList<>())
                        .build())
                .toList();

        // Map department to the response DTO
        return DepartmentResponseDTO.builder()
                .departmentId(department.getDepartmentID())
                .name(department.getName())
                .code(department.getCode())
                .head(department.getHead())
                .programs(programDTOs)
                .build();
    }
    private Department mapToDepartmentEntity(DepartmentRequestDTO departmentDTO) {
        return Department.builder()
                .name(departmentDTO.getName())
                .code(departmentDTO.getCode())
                .head(departmentDTO.getHead())
                .departmentPrograms(new HashSet<>())
                .build();
    }
}
