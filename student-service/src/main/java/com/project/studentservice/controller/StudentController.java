package com.project.studentservice.controller;

import com.project.studentservice.dto.GPAResponseDTO;
import com.project.studentservice.dto.StudentRequestDTO;
import com.project.studentservice.dto.StudentResponseDTO;
import com.project.studentservice.model.Student;
import com.project.studentservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponseDTO> registerStudent(@RequestBody StudentRequestDTO studentDTO) {
        Student student = studentService.registerStudent(studentService.toEntity(studentDTO));
        return new ResponseEntity<>(studentService.toDTO(student), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(studentService.toDTO(student));
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<StudentResponseDTO> getStudentByCardId(@PathVariable String cardId) {
        Student student = studentService.getStudentByCardId(cardId);
        return ResponseEntity.ok(studentService.toDTO(student));
    }

    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByBatch(@PathVariable Long batchId) {
        List<Student> students = studentService.getStudentsByBatch(batchId);
        return ResponseEntity.ok(students.stream().map(studentService::toDTO).collect(Collectors.toList()));
    }

    @GetMapping("/section/{section}")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsBySection(@PathVariable char section) {
        List<Student> students = studentService.getStudentsBySection(section);
        return ResponseEntity.ok(students.stream().map(studentService::toDTO).collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students.stream().map(studentService::toDTO).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(@PathVariable Long id, @RequestBody StudentRequestDTO studentDTO) {
        Student student = studentService.updateStudent(id, studentService.toEntity(studentDTO));
        return ResponseEntity.ok(studentService.toDTO(student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }
}