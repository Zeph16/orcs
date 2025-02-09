package com.project.studentservice.service;


import com.project.studentservice.dto.AcademicRecordRequestDTO;
import com.project.studentservice.dto.AcademicRecordResponseDTO;
import com.project.studentservice.exception.ResourceNotFoundException;
import com.project.studentservice.feignclient.client.CurriculumServiceClient;
import com.project.studentservice.feignclient.dto.CourseResponseDTO;
import com.project.studentservice.model.AcademicRecord;
import com.project.studentservice.model.Student;
import com.project.studentservice.repository.AcademicRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicRecordService {
    private final AcademicRecordRepository academicRecordRepository;
    private final StudentService studentService;
    private final CurriculumServiceClient curriculumServiceClient;

    public AcademicRecord createAcademicRecord(AcademicRecord academicRecord) {
        return academicRecordRepository.save(academicRecord);
    }

    public AcademicRecord getAcademicRecordById(int id) {
        return academicRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic record not found"));
    }

    public List<AcademicRecord> getAllAcademicRecords() {
        return academicRecordRepository.findAll();
    }

    public List<AcademicRecord> getAcademicRecordsByStudent(int studentId) {
        Student student = studentService.getStudentById(studentId);
        return academicRecordRepository.findByStudent(student);
    }

    public List<AcademicRecord> getAcademicRecordsByCourse(int courseId) {
        return academicRecordRepository.findByCourseId(courseId);
    }

    public List<AcademicRecord> getAcademicRecordsByTerm(int termId) {
        return academicRecordRepository.findByTermId(termId);
    }

    public AcademicRecord updateAcademicRecord(int id, AcademicRecord academicRecordDetails) {
        AcademicRecord academicRecord = getAcademicRecordById(id);
        academicRecord.setStudent(academicRecordDetails.getStudent());
        academicRecord.setCourseId(academicRecordDetails.getCourseId());
        academicRecord.setTermId(academicRecordDetails.getTermId());
        academicRecord.setMidExam(academicRecordDetails.getMidExam());
        academicRecord.setFinalExam(academicRecordDetails.getFinalExam());
        academicRecord.setAssignment(academicRecordDetails.getAssignment());
        academicRecord.setLab(academicRecordDetails.getLab());
        academicRecord.setGrade(academicRecordDetails.getGrade());
        return academicRecordRepository.save(academicRecord);
    }

    public void deleteAcademicRecord(int id) {
        AcademicRecord academicRecord = getAcademicRecordById(id);
        academicRecordRepository.delete(academicRecord);
    }

    public AcademicRecord toEntity(AcademicRecordRequestDTO dto) {
        // Look up studentId by studentCardId
        Student student = studentService.getStudentByCardId(dto.getStudentCardId());

        return AcademicRecord.builder()
                .student(student)
                .courseId(dto.getCourseId())
                .termId(dto.getTermId())
                .midExam(dto.getMidExam())
                .finalExam(dto.getFinalExam())
                .assignment(dto.getAssignment())
                .lab(dto.getLab())
                .grade(dto.getGrade())
                .build();
    }

    public AcademicRecordResponseDTO toDTO(AcademicRecord record) {
        ResponseEntity<CourseResponseDTO> courseResponse = curriculumServiceClient.getCourseById((Long.valueOf(record.getCourseId())));
        if (courseResponse.getStatusCode() != HttpStatus.OK || courseResponse.getBody() == null) {
            throw new ResourceNotFoundException("Course not found");
        }

        CourseResponseDTO course = courseResponse.getBody();

        return AcademicRecordResponseDTO.builder()
                .id(record.getId())
                .studentCardId(record.getStudent().getCardId())
                .courseCode(course.getCode())
                .courseName(course.getTitle())
                .termId(record.getTermId())
                .midExam(record.getMidExam())
                .finalExam(record.getFinalExam())
                .assignment(record.getAssignment())
                .lab(record.getLab())
                .grade(record.getGrade())
                .build();
    }

    public List<AcademicRecordResponseDTO> searchAcademicRecordsByStudentNameOrCardID(String nameOrCardIdQuery) {
        String sanitizedQuery = sanitizeSearchTerm(nameOrCardIdQuery);
        return academicRecordRepository.searchAcademicRecords(sanitizedQuery).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private String sanitizeSearchTerm(String term) {
        if (term == null) {
            return null;
        }
        return term.replaceAll("[^a-zA-Z0-9\\s\\-\\.\\,]", "").trim();
    }
}