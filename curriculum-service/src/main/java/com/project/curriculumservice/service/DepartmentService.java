package com.project.curriculumservice.service;

import com.project.curriculumservice.dto.DepartmentRequestDTO;
import com.project.curriculumservice.dto.DepartmentResponseDTO;
import com.project.curriculumservice.dto.ProgramResponseDTO;
import com.project.curriculumservice.dto.ProgramWithCreditsDTO;
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
        if (departmentDTO.getProgramCredits() == null || departmentDTO.getProgramCredits().isEmpty()) {
            throw new IllegalArgumentException("At least one program must be associated with the department");
        }

        // Create and save the department
        Department newDepartment = mapToDepartmentEntity(departmentDTO);
        final Department savedDepartment = departmentRepository.save(newDepartment); // Final variable

        // Create department-program relationships with credit hours
        departmentDTO.getProgramCredits().forEach((programId, creditHrs) ->
                departmentProgramService.createDepartmentProgram(savedDepartment, programId, creditHrs)
        );

        return getDepartmentById(savedDepartment.getDepartmentID());
    }


    public DepartmentResponseDTO getDepartmentById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));

        List<DepartmentProgram> departmentPrograms = departmentProgramService.getDepartmentPrograms(departmentId);

        return mapToDepartmentResponseDTO(department, departmentPrograms);
    }

    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(department -> {
                    List<DepartmentProgram> departmentPrograms =
                            departmentProgramService.getDepartmentPrograms(department.getDepartmentID());
                    return mapToDepartmentResponseDTO(department, departmentPrograms);
                })
                .collect(Collectors.toList());
    }

    public DepartmentResponseDTO updateDepartment(Long departmentId, DepartmentRequestDTO departmentDTO) {
        Department existingDepartment = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));

        // Update basic department information
        existingDepartment.setName(departmentDTO.getName());
        existingDepartment.setCode(departmentDTO.getCode());
        existingDepartment.setHead(departmentDTO.getHead());

        // Handle program associations with credit hours
        if (departmentDTO.getProgramCredits() != null) {
            departmentProgramService.updateDepartmentPrograms(existingDepartment, departmentDTO.getProgramCredits());
        }

        Department department = departmentRepository.save(existingDepartment);
        return getDepartmentById(department.getDepartmentID());
    }

    // Delete a department
    public void deleteDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));
        departmentRepository.delete(department);
    }

    private DepartmentResponseDTO mapToDepartmentResponseDTO(Department department, List<DepartmentProgram> departmentPrograms) {
        List<ProgramWithCreditsDTO> programDTOs = departmentPrograms.stream()
                .map(dp -> ProgramWithCreditsDTO.builder()
                        .programId(dp.getProgram().getProgramID())
                        .name(dp.getProgram().getName())
                        .code(dp.getProgram().getCode())
                        .totalRequiredCreditHrs(dp.getTotalRequiredCreditHrs())
                        .build())
                .toList();

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

    public int getTotalRequiredCreditHrs(Long departmentId, Long programId) {
        return departmentProgramService.getTotalRequiredCreditHrs(departmentId, programId);
    }
}
