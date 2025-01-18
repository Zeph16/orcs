package com.project.curriculumservice.service;

import com.project.curriculumservice.dto.ProgramDTO;
import com.project.curriculumservice.exception.ResourceNotFoundException;
import com.project.curriculumservice.model.Program;
import com.project.curriculumservice.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgramService {
    private final ProgramRepository programRepository;

    // Create a new program
    public ProgramDTO createProgram(ProgramDTO programDTO) {
        Program program = mapToEntity(programDTO);
        Program savedProgram = programRepository.save(program);
        return mapToDTO(savedProgram);
    }

    // Get a program by ID
    public ProgramDTO getProgramById(Long programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId));
        return mapToDTO(program);
    }

    // Get all programs
    public List<ProgramDTO> getAllPrograms() {
        List<Program> programs = programRepository.findAll();
        return programs.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Update an existing program
    public ProgramDTO updateProgram(Long programId, ProgramDTO programDTO) {
        Program existingProgram = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId));
        existingProgram.setName(programDTO.getName());
        existingProgram.setCode(programDTO.getCode());
        Program updatedProgram = programRepository.save(existingProgram);
        return mapToDTO(updatedProgram);
    }

    // Delete a program
    public void deleteProgram(Long programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with ID: " + programId));
        programRepository.delete(program);
    }

    // Map ProgramDTO to Program entity
    private Program mapToEntity(ProgramDTO programDTO) {
        return Program.builder()
                .programID(programDTO.getProgramId())
                .name(programDTO.getName())
                .code(programDTO.getCode())
                .build();
    }

    // Map Program entity to ProgramDTO
    private ProgramDTO mapToDTO(Program program) {
        return ProgramDTO.builder()
                .programId(program.getProgramID())
                .name(program.getName())
                .code(program.getCode())
                .build();
    }
}
