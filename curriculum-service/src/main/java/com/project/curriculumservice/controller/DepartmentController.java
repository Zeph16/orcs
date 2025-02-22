package com.project.curriculumservice.controller;

import com.project.curriculumservice.dto.DepartmentRequestDTO;
import com.project.curriculumservice.dto.DepartmentResponseDTO;
import com.project.curriculumservice.model.Department;
import com.project.curriculumservice.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    // Create a new department
    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@Valid @RequestBody DepartmentRequestDTO departmentDTO) {
        DepartmentResponseDTO createdDepartment = departmentService.createDepartment(departmentDTO);
        return new ResponseEntity<>(createdDepartment, HttpStatus.CREATED);
    }

    // Get a department by ID
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentById(@PathVariable Long id) {
        DepartmentResponseDTO department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    // Get all departments
    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        List<DepartmentResponseDTO> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{departmentId}/{programId}")
    public ResponseEntity<Integer> getTotalRequiredCreditHrs(@PathVariable Long departmentId, @PathVariable Long programId) {
        int totalCreditHrs = departmentService.getTotalRequiredCreditHrs(departmentId, programId);
        return ResponseEntity.ok(totalCreditHrs);
    }

    // Update an existing department
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentRequestDTO departmentDTO) {
        DepartmentResponseDTO updatedDepartment = departmentService.updateDepartment(id, departmentDTO);
        return ResponseEntity.ok(updatedDepartment);
    }

    // Delete a department
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
