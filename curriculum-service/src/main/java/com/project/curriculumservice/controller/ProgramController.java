package com.project.curriculumservice.controller;

import com.project.curriculumservice.dto.ProgramRequestDTO;
import com.project.curriculumservice.dto.ProgramResponseDTO;
import com.project.curriculumservice.service.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/programs")
@RequiredArgsConstructor
public class ProgramController {
    private final ProgramService programService;

    // Create a new program
    @PostMapping
    public ResponseEntity<ProgramResponseDTO> createProgram(@Valid @RequestBody ProgramRequestDTO programDTO) {
        ProgramResponseDTO createdProgram = programService.createProgram(programDTO);
        return new ResponseEntity<>(createdProgram, HttpStatus.CREATED);
    }

    // Get a program by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProgramResponseDTO> getProgramById(@PathVariable Long id) {
        ProgramResponseDTO program = programService.getProgramById(id);
        return ResponseEntity.ok(program);
    }

    // Get all programs
    @GetMapping
    public ResponseEntity<List<ProgramResponseDTO>> getAllPrograms() {
        List<ProgramResponseDTO> programs = programService.getAllPrograms();
        return ResponseEntity.ok(programs);
    }

    // Update an existing program
    @PutMapping("/{id}")
    public ResponseEntity<ProgramResponseDTO> updateProgram(@PathVariable Long id, @Valid @RequestBody ProgramRequestDTO programDetails) {
        ProgramResponseDTO updatedProgram = programService.updateProgram(id, programDetails);
        return ResponseEntity.ok(updatedProgram);
    }

    // Delete a program
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgram(@PathVariable Long id) {
        programService.deleteProgram(id);
        return ResponseEntity.noContent().build();
    }
}
