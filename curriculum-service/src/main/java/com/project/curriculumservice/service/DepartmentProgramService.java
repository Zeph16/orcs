package com.project.curriculumservice.service;

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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    public DepartmentProgram createDepartmentProgram(Program program, Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + departmentId));

        DepartmentProgramId id = new DepartmentProgramId(departmentId, program.getProgramID());
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

    @Transactional
    public void updateDepartmentPrograms(Department department, Set<Long> newProgramIds) {
        // Get existing associations
        List<DepartmentProgram> existingAssociations = departmentProgramRepository
                .findByDepartment_DepartmentID(department.getDepartmentID());

        // Get existing program IDs
        Set<Long> existingProgramIds = existingAssociations.stream()
                .map(dp -> dp.getProgram().getProgramID())
                .collect(Collectors.toSet());

        // Find programs to remove (in existing but not in new)
        existingAssociations.stream()
                .filter(dp -> !newProgramIds.contains(dp.getProgram().getProgramID()))
                .forEach(dp -> departmentProgramRepository.delete(dp));

        // Add new associations (in new but not in existing)
        newProgramIds.stream()
                .filter(programId -> !existingProgramIds.contains(programId))
                .forEach(programId -> createDepartmentProgram(department, programId));
    }
    @Transactional
    public void updateDepartmentPrograms(Program program, Set<Long> newDepartmentIds) {
        // Get existing associations
        List<DepartmentProgram> existingAssociations = departmentProgramRepository
                .findByProgram_ProgramID(program.getProgramID());

        // Get existing department IDs
        Set<Long> existingProgramIds = existingAssociations.stream()
                .map(prog -> prog.getDepartment().getDepartmentID())
                .collect(Collectors.toSet());

        // Find departments to remove (in existing but not in new)
        existingAssociations.stream()
                .filter(prog -> !newDepartmentIds.contains(prog.getDepartment().getDepartmentID()))
                .forEach(prog -> departmentProgramRepository.delete(prog));

        // Add new associations (in new but not in existing)
        newDepartmentIds.stream()
                .filter(departmentId -> !existingProgramIds.contains(departmentId))
                .forEach(departmentId -> createDepartmentProgram(program, departmentId));
    }
}
