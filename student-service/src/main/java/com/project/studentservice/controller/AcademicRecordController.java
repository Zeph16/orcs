package com.project.studentservice.controller;


import com.project.studentservice.dto.AcademicRecordRequestDTO;
import com.project.studentservice.dto.AcademicRecordResponseDTO;
import com.project.studentservice.dto.GPAResponseDTO;
import com.project.studentservice.model.AcademicRecord;
import com.project.studentservice.service.AcademicRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/academic-records")
@RequiredArgsConstructor
public class AcademicRecordController {
    private final AcademicRecordService academicRecordService;

    @PostMapping
    public ResponseEntity<AcademicRecordResponseDTO> createAcademicRecord(@RequestBody AcademicRecordRequestDTO recordDTO) {
        // call enrollment service and update enrollment
        AcademicRecord record = academicRecordService.createAcademicRecord(academicRecordService.toEntity(recordDTO));
        return new ResponseEntity<>(academicRecordService.toDTO(record), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicRecordResponseDTO> getAcademicRecordById(@PathVariable Long id) {
        AcademicRecord record = academicRecordService.getAcademicRecordById(id);
        return ResponseEntity.ok(academicRecordService.toDTO(record));
    }

    @GetMapping
    public ResponseEntity<List<AcademicRecordResponseDTO>> getAllAcademicRecords() {
        List<AcademicRecord> records = academicRecordService.getAllAcademicRecords();
        return ResponseEntity.ok(records.stream().map(academicRecordService::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AcademicRecordResponseDTO>> getAcademicRecordsByStudent(@PathVariable Long studentId) {
        List<AcademicRecord> records = academicRecordService.getAcademicRecordsByStudent(studentId);
        return ResponseEntity.ok(records.stream().map(academicRecordService::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AcademicRecordResponseDTO>> searchAcademicRecordsByStudentNameOrCardID(@RequestParam String nameOrCardIdQuery) {
        return ResponseEntity.ok(academicRecordService.searchAcademicRecordsByStudentNameOrCardID(nameOrCardIdQuery));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AcademicRecordResponseDTO>> getAcademicRecordsByCourse(@PathVariable Long courseId) {
        List<AcademicRecord> records = academicRecordService.getAcademicRecordsByCourse(courseId);
        return ResponseEntity.ok(records.stream().map(academicRecordService::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/term/{termId}")
    public ResponseEntity<List<AcademicRecordResponseDTO>> getAcademicRecordsByTerm(@PathVariable Long termId) {
        List<AcademicRecord> records = academicRecordService.getAcademicRecordsByTerm(termId);
        return ResponseEntity.ok(records.stream().map(academicRecordService::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/gpa/{studentId}")
    public ResponseEntity<GPAResponseDTO> getStudentGPA(@PathVariable Long studentId) {
        GPAResponseDTO gpaResponseDTO = academicRecordService.calculateFullGPA(studentId);
        return ResponseEntity.ok(gpaResponseDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcademicRecordResponseDTO> updateAcademicRecord(@PathVariable Long id, @RequestBody AcademicRecordRequestDTO recordDTO) {
        AcademicRecord record = academicRecordService.updateAcademicRecord(id, academicRecordService.toEntity(recordDTO));
        return ResponseEntity.ok(academicRecordService.toDTO(record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAcademicRecord(@PathVariable Long id) {
        academicRecordService.deleteAcademicRecord(id);
        return ResponseEntity.noContent().build();
    }

    // get student gpa
    // get student major gpa
    // get gpa by term
}