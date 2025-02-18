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
import java.util.Random;


@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;
    private final BatchService batchService;
    private final Random random = new Random();

    public Student registerStudent(Student student) {
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Student with this email already exists");
        }

        Batch batch = student.getBatch();
        student.setBatch(batch);
        student.setCardId(generateCardId());
        return studentRepository.save(student);
    }

    public Student getStudentById(Long id) {
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

    public List<Student> getStudentsByBatch(Long batchId) {
        return studentRepository.findByBatchId(batchId);
    }

    public List<Student> getStudentsBySection(char section) {
        return studentRepository.findByBatchSection(section);
    }

    public Student updateStudent(Long id, Student studentDetails) {
        Student student = getStudentById(id);
        student.setName(studentDetails.getName());
        student.setCardId(studentDetails.getCardId());
        student.setEmail(studentDetails.getEmail());
        student.setPhone(studentDetails.getPhone());
        student.setEmergencyContact(studentDetails.getEmergencyContact());
        student.setAddress(studentDetails.getAddress());
        student.setEnrollmentStatus(studentDetails.getEnrollmentStatus());
        student.setBatch(studentDetails.getBatch());
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
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
                .emergencyContact(dto.getEmergencyContact())
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
                .emergencyContact(student.getEmergencyContact())
                .batch(batchService.toDTO(student.getBatch()))
                .build();
    }

    public String generateCardId() {
        String code;
        do {
            char first = (char) ('A' + random.nextInt(26));
            char second = (char) ('A' + random.nextInt(26));
            int number = random.nextInt(10000);
            code = String.format("%c%c%04d", first, second, number);
        } while (studentRepository.findByCardId(code).isPresent());

        return code;
    }
}