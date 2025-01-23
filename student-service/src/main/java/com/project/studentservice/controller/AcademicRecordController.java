package com.project.studentservice.controller;


import com.project.studentservice.dto.AcademicRecordRequestDTO;
import com.project.studentservice.dto.AcademicRecordResponseDTO;
import com.project.studentservice.model.AcademicRecord;
import com.project.studentservice.service.AcademicRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/academic-records")
@RequiredArgsConstructor
public class AcademicRecordController {
    private final AcademicRecordService academicRecordService;

    @PostMapping
    public ResponseEntity<AcademicRecordResponseDTO> createAcademicRecord(@RequestBody AcademicRecordRequestDTO recordDTO) {
        AcademicRecord record = academicRecordService.createAcademicRecord(academicRecordService.toEntity(recordDTO));
        return new ResponseEntity<>(academicRecordService.toDTO(record), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AcademicRecordResponseDTO> getAcademicRecordById(@PathVariable int id) {
        AcademicRecord record = academicRecordService.getAcademicRecordById(id);
        return ResponseEntity.ok(academicRecordService.toDTO(record));
    }

    @GetMapping
    public ResponseEntity<List<AcademicRecordResponseDTO>> getAllAcademicRecords() {
        List<AcademicRecord> records = academicRecordService.getAllAcademicRecords();
        return ResponseEntity.ok(records.stream().map(academicRecordService::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AcademicRecordResponseDTO>> getAcademicRecordsByStudent(@PathVariable int studentId) {
        List<AcademicRecord> records = academicRecordService.getAcademicRecordsByStudent(studentId);
        return ResponseEntity.ok(records.stream().map(academicRecordService::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AcademicRecordResponseDTO>> getAcademicRecordsByCourse(@PathVariable int courseId) {
        List<AcademicRecord> records = academicRecordService.getAcademicRecordsByCourse(courseId);
        return ResponseEntity.ok(records.stream().map(academicRecordService::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/term/{termId}")
    public ResponseEntity<List<AcademicRecordResponseDTO>> getAcademicRecordsByTerm(@PathVariable int termId) {
        List<AcademicRecord> records = academicRecordService.getAcademicRecordsByTerm(termId);
        return ResponseEntity.ok(records.stream().map(academicRecordService::toDTO).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AcademicRecordResponseDTO> updateAcademicRecord(@PathVariable int id, @RequestBody AcademicRecordRequestDTO recordDTO) {
        AcademicRecord record = academicRecordService.updateAcademicRecord(id, academicRecordService.toEntity(recordDTO));
        return ResponseEntity.ok(academicRecordService.toDTO(record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAcademicRecord(@PathVariable int id) {
        academicRecordService.deleteAcademicRecord(id);
        return ResponseEntity.noContent().build();
    }
}