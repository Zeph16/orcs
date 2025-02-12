package com.project.studentservice.service;


import com.project.studentservice.dto.AcademicRecordRequestDTO;
import com.project.studentservice.dto.AcademicRecordResponseDTO;
import com.project.studentservice.dto.GPAResponseDTO;
import com.project.studentservice.exception.ResourceNotFoundException;
import com.project.studentservice.feignclient.client.CurriculumServiceClient;
import com.project.studentservice.feignclient.client.EnrollmentServiceClient;
import com.project.studentservice.feignclient.dto.*;
import com.project.studentservice.model.AcademicRecord;
import com.project.studentservice.model.Grade;
import com.project.studentservice.model.Student;
import com.project.studentservice.repository.AcademicRecordRepository;
import com.project.studentservice.util.GradeConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicRecordService {
    private final AcademicRecordRepository academicRecordRepository;
    private final StudentService studentService;
    private final CurriculumServiceClient curriculumServiceClient;
    private final EnrollmentServiceClient enrollmentServiceClient;
    private final GradeConfiguration gradeConfiguration;

    public AcademicRecord createAcademicRecord(AcademicRecord academicRecord) {
        ResponseEntity<List<EnrollmentResponseDTO>> enrollmentResponse = enrollmentServiceClient.getEnrollmentsByStudent(academicRecord.getStudent().getId(), List.of("ENROLLED"));
        if (enrollmentResponse.getStatusCode() != HttpStatus.OK || enrollmentResponse.getBody() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }
        List<EnrollmentResponseDTO> enrollments = enrollmentResponse.getBody().stream().filter(e -> e.getCourseOffering().getCourse().getCourseId().equals(academicRecord.getCourseId())).toList();
        if (enrollments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found");
        }

        EnrollmentStatus status = EnrollmentStatus.COMPLETED;
        if (academicRecord.getGrade().equals(Grade.RC) ||
            academicRecord.getGrade().equals(Grade.RA)) {
            status = EnrollmentStatus.NOT_COMPLETED;
        }
        CourseOfferingResponseDTO courseOffering = enrollments.get(0).getCourseOffering();
        ResponseEntity<EnrollmentResponseDTO> updateResponse = enrollmentServiceClient.updateEnrollment(enrollments.get(0).getEnrollmentID(), EnrollmentRequestDTO.builder()
                        .enrollmentID(enrollments.get(0).getEnrollmentID())
                        .offeringID(courseOffering.getOfferingID())
                        .status(status)
                        .studentID(academicRecord.getStudent().getId())
                        .type(enrollments.get(0).getType())
                .build());

        if (updateResponse.getStatusCode() != HttpStatus.OK || updateResponse.getBody() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not update enrollment status");
        }

        return academicRecordRepository.save(academicRecord);
    }

    public AcademicRecord getAcademicRecordById(Long id) {
        return academicRecordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Academic record not found"));
    }

    public List<AcademicRecord> getAllAcademicRecords() {
        return academicRecordRepository.findAll();
    }

    public List<AcademicRecord> getAcademicRecordsByStudent(Long studentId) {
        Student student = studentService.getStudentById(studentId);
        return academicRecordRepository.findByStudent(student);
    }

    public List<AcademicRecord> getAcademicRecordsByCourse(Long courseId) {
        return academicRecordRepository.findByCourseId(courseId);
    }

    public List<AcademicRecord> getAcademicRecordsByTerm(Long termId) {
        return academicRecordRepository.findByTermId(termId);
    }

    public AcademicRecord updateAcademicRecord(Long id, AcademicRecord academicRecordDetails) {
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

    public void deleteAcademicRecord(Long id) {
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

    public GPAResponseDTO calculateFullGPA(Long studentId) {
        List<AcademicRecord> records = getAcademicRecordsByStudent(studentId);
        Map<String, Double> gradePoints = gradeConfiguration.getGradePoints();

        double totalPoints = 0;
        int totalCredits = 0;
        double majorPoints = 0;
        int majorCredits = 0;

        Map<Long, Double> termTotalPoints = new HashMap<>();
        Map<Long, Integer> termTotalCredits = new HashMap<>();
        Map<Long, Double> termMajorPoints = new HashMap<>();
        Map<Long, Integer> termMajorCredits = new HashMap<>();

        for (AcademicRecord record : records) {
            ResponseEntity<CourseResponseDTO> courseResponse = curriculumServiceClient.getCourseById(record.getCourseId());
            if (courseResponse.getStatusCode() != HttpStatus.OK || courseResponse.getBody() == null) {
                throw new ResourceNotFoundException("Course not found");
            }
            CourseResponseDTO course = courseResponse.getBody();

            Double gradePoint = gradePoints.get(record.getGrade().name());
            if (gradePoint == null) continue;

            int creditHours = course.getCreditHrs();
            totalPoints += gradePoint * creditHours;
            totalCredits += creditHours;

            termTotalPoints.merge(record.getTermId(), gradePoint * creditHours, Double::sum);
            termTotalCredits.merge(record.getTermId(), creditHours, Integer::sum);

            if (course.getType() == CourseType.MAJOR) {
                majorPoints += gradePoint * creditHours;
                majorCredits += creditHours;
                termMajorPoints.merge(record.getTermId(), gradePoint * creditHours, Double::sum);
                termMajorCredits.merge(record.getTermId(), creditHours, Integer::sum);
            }
        }

        GPAResponseDTO.GPASingle overallGPA = GPAResponseDTO.GPASingle.builder()
                .cumulativeGPA(totalCredits == 0 ? 0.0 : totalPoints / totalCredits)
                .majorGPA(majorCredits == 0 ? 0.0 : majorPoints / majorCredits)
                .build();

        Map<Long, GPAResponseDTO.GPASingle> termGPAs = new HashMap<>();
        for (Long termId : termTotalPoints.keySet()) {
            termGPAs.put(termId, GPAResponseDTO.GPASingle.builder()
                    .cumulativeGPA(termTotalCredits.get(termId) == 0 ? 0.0 : termTotalPoints.get(termId) / termTotalCredits.get(termId))
                    .majorGPA(termMajorCredits.getOrDefault(termId, 0) == 0 ? 0.0 : termMajorPoints.getOrDefault(termId, 0.0) / termMajorCredits.getOrDefault(termId, 1))
                    .build());
        }

        return GPAResponseDTO.builder()
                .totalGPA(overallGPA)
                .gpaPerTermList(termGPAs)
                .build();
    }
}