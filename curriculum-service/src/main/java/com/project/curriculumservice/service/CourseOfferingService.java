package com.project.curriculumservice.service;

import com.project.curriculumservice.dto.*;
import com.project.curriculumservice.exception.ResourceNotFoundException;
import com.project.curriculumservice.feignclient.client.EnrollmentServiceClient;
import com.project.curriculumservice.feignclient.client.StudentServiceClient;
import com.project.curriculumservice.feignclient.dtos.BatchResponseDTO;
import com.project.curriculumservice.feignclient.dtos.EnrollmentStatus;
import com.project.curriculumservice.feignclient.dtos.StudentResponseDTO;
import com.project.curriculumservice.model.*;
import com.project.curriculumservice.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseOfferingService {
    private final CourseOfferingRepository courseOfferingRepository;
    private final CourseService courseService;
    private final TermService termService;
    private final StudentServiceClient studentServiceClient;
    private final EnrollmentServiceClient enrollmentServiceClient;

    public CourseOfferingResponseDTO createCourseOffering(CourseOfferingRequestDTO requestDTO) {
        // Validate references exist
        CourseResponseDTO course = courseService.getCourseById(requestDTO.getCourseId());
        TermResponseDTO term = termService.getTermById(requestDTO.getTermId());

        CourseOffering courseOffering = CourseOffering.builder()
                .course(Course.builder()
                        .courseID(course.getCourseId())
                        .title(course.getTitle())
                        .code(course.getCode())
                        .creditHrs(course.getCreditHrs())
                        .type(course.getType())
                        .build()
                )
                .term(Term.builder()
                        .termID(term.getTermId())
                        .code(term.getCode())
                        .build())
                .batchID(requestDTO.getBatchId())
                .capacity(requestDTO.getCapacity())
                .availableSeats(requestDTO.getCapacity())
                .instructorName(requestDTO.getInstructorName())
                .build();

        courseOfferingRepository.save(courseOffering);
        return mapToCourseOfferingResponseDTO(courseOffering);
    }

    public CourseOfferingResponseDTO updateCourseOffering(Long offeringId, CourseOfferingRequestDTO requestDTO) {
        CourseOffering existingOffering = courseOfferingRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Course Offering not found with ID: " + offeringId));

        // Update references if changed
        if (!existingOffering.getCourse().getCourseID().equals(requestDTO.getCourseId())) {
            CourseResponseDTO course = courseService.getCourseById(requestDTO.getCourseId());
            existingOffering.setCourse(Course.builder().courseID(course.getCourseId()).build());
        }

        if (!existingOffering.getTerm().getTermID().equals(requestDTO.getTermId())) {
            TermResponseDTO term = termService.getTermById(requestDTO.getTermId());
            existingOffering.setTerm(Term.builder().termID(term.getTermId()).build());
        }

        if (!existingOffering.getBatchID().equals(requestDTO.getBatchId())) {
            BatchResponseDTO batch = studentServiceClient.getBatchById(requestDTO.getBatchId()).getBody();
            existingOffering.setBatchID(batch.getId());
        }

        // Update capacity and available seats
        int oldCapacity = existingOffering.getCapacity();
        int newCapacity = requestDTO.getCapacity();
        int currentAvailableSeats = existingOffering.getAvailableSeats();

        existingOffering.setCapacity(newCapacity);
        // Adjust available seats proportionally
        existingOffering.setAvailableSeats(currentAvailableSeats + (newCapacity - oldCapacity));

        existingOffering.setInstructorName(requestDTO.getInstructorName());

        CourseOffering updatedOffering = courseOfferingRepository.save(existingOffering);
        return mapToCourseOfferingResponseDTO(updatedOffering);
    }

    public List<CourseOfferingResponseDTO> getCourseOfferingsForStudent(int studentID){
        String currentTermCode = termService.getCurrentTermCode();
        StudentResponseDTO studentResponseDTO = studentServiceClient.getStudentById(studentID).getBody();
        List<CourseOffering> allCourseOfferings = courseOfferingRepository.findByDepartmentIdAndProgramIdAndTermCode(studentResponseDTO.getBatch().getProgramId(), studentResponseDTO.getBatch().getDepartmentId(), currentTermCode);
        List<CourseOfferingResponseDTO> enrolledAndCompletedEnrollments =
                enrollmentServiceClient.getEnrollmentsByStudent(studentID, Arrays.asList("COMPLETED", "ENROLLED"))
                        .getBody()
                        .stream()
                        .map(enrollment -> enrollment.getCourseOffering())
                        .collect(Collectors.toList());

        Set<String> enrolledAndCompletedCourseCodes = enrolledAndCompletedEnrollments.stream()
                .map(enrollment -> enrollment.getCourse().getCode()) // Extract course code
                .collect(Collectors.toSet());
        List<CourseOffering> unEnrolledCourseOfferings = allCourseOfferings.stream()
                .filter(courseOffering -> !enrolledAndCompletedCourseCodes.contains(courseOffering.getCourse().getCode())) // Exclude completed courses
                .collect(Collectors.toList());

        List<CourseOfferingResponseDTO> eligibleCourseOfferings = new ArrayList<>();

        for (CourseOffering courseOffering : unEnrolledCourseOfferings) {
            List<PrerequisiteDTO> prerequisites = courseService.getPrerequisitesById(courseOffering.getCourse().getCourseID());

            // Check if all prerequisites are completed
            boolean allPrerequisitesCompleted = prerequisites.size() == 0 || prerequisites.stream()
                    .allMatch(prerequisite -> enrolledAndCompletedCourseCodes.contains(prerequisite.getCode()));

            if (allPrerequisitesCompleted) {
                // Map to CourseOfferingResponseDTO and add to eligible list
                eligibleCourseOfferings.add(mapToCourseOfferingResponseDTO(courseOffering));
            }
        }

        return eligibleCourseOfferings;
    }
    public List<CourseOfferingResponseDTO> searchCourseOfferings(String query) {
        String sanitizedQuery = sanitizeSearchTerm(query);
        return courseOfferingRepository.searchCourseOfferings(sanitizedQuery).stream()
                .map(this::mapToCourseOfferingResponseDTO)
                .collect(Collectors.toList());
    }

    public List<CourseOfferingResponseDTO> getCourseOfferingsByBatchId(Long batchId) {
        return courseOfferingRepository.searchCourseOfferingsByBatchID(batchId).stream()
                .map(this::mapToCourseOfferingResponseDTO)
                .collect(Collectors.toList());
    }

    public CourseOfferingResponseDTO getCourseOfferingById(Long offeringId) {
        CourseOffering courseOffering = courseOfferingRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Course Offering not found with ID: " + offeringId));
        return mapToCourseOfferingResponseDTO(courseOffering);
    }

    public List<CourseOfferingResponseDTO> getAllCourseOfferings() {
        return courseOfferingRepository.findAll().stream()
                .map(this::mapToCourseOfferingResponseDTO)
                .collect(Collectors.toList());
    }

    public void deleteCourseOffering(Long offeringId) {
        CourseOffering courseOffering = courseOfferingRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Course Offering not found with ID: " + offeringId));
        courseOfferingRepository.delete(courseOffering);
    }

    private String sanitizeSearchTerm(String term) {
        if (term == null) {
            return null;
        }
        return term.replaceAll("[^a-zA-Z0-9\\s\\-\\.\\,]", "").trim();
    }

    private CourseOfferingResponseDTO mapToCourseOfferingResponseDTO(CourseOffering courseOffering) {
        return CourseOfferingResponseDTO.builder()
                .offeringID(courseOffering.getOfferingID())
                .course(CourseResponseDTO.builder()
                        .courseId(courseOffering.getCourse().getCourseID())
                        .title(courseOffering.getCourse().getTitle())
                        .code(courseOffering.getCourse().getCode())
                        .creditHrs(courseOffering.getCourse().getCreditHrs())
                        .type(courseOffering.getCourse().getType())
                        .build())
                .batch(studentServiceClient.getBatchById(courseOffering.getBatchID()).getBody())
                .term(TermResponseDTO.builder()
                        .termId(courseOffering.getTerm().getTermID())
                        .code(courseOffering.getTerm().getCode())
                        .academicYear(courseOffering.getTerm().getAcademicYear())
                        .season(courseOffering.getTerm().getSeason())
                        .build())
                .capacity(courseOffering.getCapacity())
                .availableSeats(courseOffering.getAvailableSeats())
                .instructorName(courseOffering.getInstructorName())
                .build();
    }
}