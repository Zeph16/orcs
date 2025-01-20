package com.project.curriculumservice.service;

import com.project.curriculumservice.dto.DepartmentResponseDTO;
import com.project.curriculumservice.dto.ProgramRequestDTO;
import com.project.curriculumservice.dto.ProgramResponseDTO;
import com.project.curriculumservice.exception.ProgramReferencedException;
import com.project.curriculumservice.exception.ResourceNotFoundException;
import com.project.curriculumservice.model.Department;
import com.project.curriculumservice.model.DepartmentProgram;
import com.project.curriculumservice.model.Program;
import com.project.curriculumservice.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService {
    private final ProgramRepository programRepository;
    private final DepartmentProgramService departmentProgramService;

    public ProgramResponseDTO createProgram(ProgramRequestDTO programDTO) {
        Program program = mapToProgramEntity(programDTO);
        program = programRepository.save(program);

        // Create department-program relationships if departments are specified
        if (!programDTO.getDepartmentIds().isEmpty()) {
            for (Long departmentId : programDTO.getDepartmentIds()) {
                departmentProgramService.createDepartmentProgram(departmentId, program.getProgramID());
            }
        }
        return mapToProgramResponseDTO(program, departmentProgramService.getDepartments(program.getProgramID()));
    }

    public ProgramResponseDTO updateProgram(Long programId, ProgramRequestDTO programDTO) {
        Program existingProgram = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId));

        existingProgram.setName(programDTO.getName());
        existingProgram.setCode(programDTO.getCode());

        // Update department relationships
        if (programDTO.getDepartmentIds() != null) {
            existingProgram.getDepartmentPrograms().clear();
            programRepository.save(existingProgram); // Save to clear relationships

            for (Long departmentId : programDTO.getDepartmentIds()) {
                departmentProgramService.createDepartmentProgram(departmentId, programId);
            }
        }

        Program updatedProgram = programRepository.save(existingProgram);

        return mapToProgramResponseDTO(updatedProgram, departmentProgramService.getDepartments(programId));
    }
    // Get a program by ID
    public ProgramResponseDTO getProgramById(Long programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId));
        // Fetch associated departments using departmentProgramRepository
        List<Department> departments = departmentProgramService.getDepartments(program.getProgramID());

        return mapToProgramResponseDTO(program, departments);
    }

    // Get all programs
    public List<ProgramResponseDTO> getAllPrograms() {
        return programRepository.findAll().stream()
                .map(program -> {
                    List<Department> departments = departmentProgramService.getDepartments(program.getProgramID());
                    return  mapToProgramResponseDTO(program, departments);
                })
                .collect(Collectors.toList());

    }

    // Delete a program
    public void deleteProgram(Long programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId));

        // Check if the program is referenced by any departments
        boolean isReferenced = !departmentProgramService.getDepartments(programId).isEmpty();

        if (isReferenced) {
            throw new ProgramReferencedException("Program cannot be deleted because it is referenced by one or more departments.");
        }

        programRepository.delete(program);
    }

    // Map ProgramDTO to Program entity
    private Program mapToProgramEntity(ProgramRequestDTO programDTO) {
        return Program.builder()
                .programID(programDTO.getProgramId())
                .name(programDTO.getName())
                .code(programDTO.getCode())
                .departmentPrograms(new HashSet<>())
                .build();
    }

    private ProgramResponseDTO mapToProgramResponseDTO(Program program, List<Department> departments) {
        // Map programs to the required DTO format
        List<DepartmentResponseDTO> departmentDTOs = departments.stream()
                .map(department -> DepartmentResponseDTO.builder()
                        .departmentId(department.getDepartmentID())
                        .name(department.getName())
                        .code(department.getCode())
                        .head(department.getHead())
                        .programs(new ArrayList<>())
                        .build())
                .toList();

        // Map department to the response DTO
        return ProgramResponseDTO.builder()
                .programId(program.getProgramID())
                .name(program.getName())
                .code(program.getCode())
                .departments(departmentDTOs)
                .build();
    }
}
