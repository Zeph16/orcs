package com.project.studentservice.service;


import com.project.studentservice.dto.StudentRequestDTO;
import com.project.studentservice.dto.StudentResponseDTO;
import com.project.studentservice.exception.DuplicateResourceException;
import com.project.studentservice.exception.ResourceNotFoundException;
import com.project.studentservice.model.Batch;
import com.project.studentservice.model.Student;
import com.project.studentservice.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final BatchService batchService;

    public Student registerStudent(Student student, Long batchId) {
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Student with this email already exists");
        }

        if (studentRepository.findByCardId(student.getCardId()).isPresent()) {
            throw new DuplicateResourceException("Student with this card ID already exists");
        }

        Batch batch = batchService.getBatchById(batchId);
        student.setBatch(batch);
        return studentRepository.save(student);
    }

    public Student getStudentById(int id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID:" + id));
    }

    public Student getStudentByCardId(String cardId) {
        return studentRepository.findByCardId(cardId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with card ID: " + cardId));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByBatch(int batchId) {
        return studentRepository.findByBatchId(batchId);
    }

    public List<Student> getStudentsBySection(char section) {
        return studentRepository.findByBatchSection(section);
    }

    public Student updateStudent(int id, Student studentDetails) {
        Student student = getStudentById(id);
        student.setName(studentDetails.getName());
        student.setCardId(studentDetails.getCardId());
        student.setEmail(studentDetails.getEmail());
        student.setPhone(studentDetails.getPhone());
        student.setAddress(studentDetails.getAddress());
        student.setEnrollmentStatus(studentDetails.getEnrollmentStatus());
        student.setBatch(studentDetails.getBatch());
        return studentRepository.save(student);
    }

    public void deleteStudent(int id) {
        Student student = getStudentById(id);
        studentRepository.delete(student);
    }

    public Student toEntity(StudentRequestDTO dto) {
        return Student.builder()
                .name(dto.getName())
                .cardId(dto.getCardId())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .enrollmentStatus(dto.getEnrollmentStatus())
                .batch(batchService.getBatchById(dto.getBatchId()))
                .build();
    }

    public StudentResponseDTO toDTO(Student student) {
        return StudentResponseDTO.builder()
                .id(student.getId())
                .name(student.getName())
                .cardId(student.getCardId())
                .email(student.getEmail())
                .phone(student.getPhone())
                .address(student.getAddress())
                .enrollmentStatus(student.getEnrollmentStatus())
                .batch(batchService.toDTO(student.getBatch()))
                .build();
    }
}