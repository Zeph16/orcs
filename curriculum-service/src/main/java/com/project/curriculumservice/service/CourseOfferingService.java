package com.project.curriculumservice.service;

import com.project.curriculumservice.dto.*;
import com.project.curriculumservice.exception.DuplicateResourceException;
import com.project.curriculumservice.exception.ResourceNotFoundException;
import com.project.curriculumservice.feignclient.client.EnrollmentServiceClient;
import com.project.curriculumservice.feignclient.client.StudentServiceClient;
import com.project.curriculumservice.feignclient.dtos.BatchResponseDTO;
import com.project.curriculumservice.feignclient.dtos.EnrollmentResponseDTO;
import com.project.curriculumservice.feignclient.dtos.EnrollmentStatus;
import com.project.curriculumservice.feignclient.dtos.StudentResponseDTO;
import com.project.curriculumservice.model.*;
import com.project.curriculumservice.repository.*;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;

import java.util.*;
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

        // Check if a course offering already exists for the same course and term
        Optional<CourseOffering> existingOffering = courseOfferingRepository.findByCourse_CourseIDAndTerm_TermID(
                requestDTO.getCourseId(),
                requestDTO.getTermId()
        );

        if (existingOffering.isPresent()) {
            throw new DuplicateResourceException("A course offering for this course already exists in the specified term.");
        }

        // Create and save the new course offering
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

    public void decrementAvailableSeats(Long offeringId) {
        CourseOffering courseOffering = courseOfferingRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Course Offering not found with ID: " + offeringId));

        if (courseOffering.getAvailableSeats() > 0) {
            courseOffering.setAvailableSeats(courseOffering.getAvailableSeats() - 1);
            courseOfferingRepository.save(courseOffering);
        } else {
            throw new IllegalStateException("No available seats to decrement for course offering with ID: " + offeringId);
        }
    }


    public CourseOfferingResponseDTO updateCourseOffering(Long offeringId, CourseOfferingRequestDTO requestDTO) {
        CourseOffering existingOffering = courseOfferingRepository.findById(offeringId)
                .orElseThrow(() -> new ResourceNotFoundException("Course Offering not found with ID: " + offeringId));

        // Update references if changed
        if (!existingOffering.getCourse().getCourseID().equals(requestDTO.getCourseId())) {
            CourseResponseDTO course = courseService.getCourseById(requestDTO.getCourseId());
            existingOffering.setCourse(
                    Course.builder()
                    .courseID(course.getCourseId())
                    .title(course.getTitle())
                    .code(course.getCode())
                    .build()
            );
        }

        if (!existingOffering.getTerm().getTermID().equals(requestDTO.getTermId())) {
            TermResponseDTO term = termService.getTermById(requestDTO.getTermId());
            existingOffering.setTerm(
                    Term.builder()
                    .termID(term.getTermId())
                    .code(term.getCode())
                    .build()
            );
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

    public List<CourseOfferingResponseDTO> getCourseOfferingsForStudent(Long studentID) {
        String currentTermCode = termService.getCurrentTermCode();

        // Check if the student has any enrollments in the current term
        List<EnrollmentResponseDTO> currentTermEnrollments = enrollmentServiceClient.getEnrollmentsByStudent(studentID, Arrays.asList("ENROLLED"))
                .getBody()
                .stream()
                .filter(enrollment -> enrollment.getCourseOffering().getTerm().getCode().equals(currentTermCode))
                .collect(Collectors.toList());

        // If the student has any enrollments in the current term, return an empty list
        if (!currentTermEnrollments.isEmpty()) {
            return Collections.emptyList();
        }

        StudentResponseDTO studentResponseDTO = studentServiceClient.getStudentById(studentID).getBody();

        // Fetch all course offerings for the student's department, program, and current term
        List<CourseOffering> allCourseOfferings = courseOfferingRepository.findByDepartmentIdAndProgramIdAndTermCode(
                studentResponseDTO.getBatch().getProgramId(),
                studentResponseDTO.getBatch().getDepartmentId(),
                currentTermCode
        );

        // Fetch enrolled and completed enrollments for the student
        List<CourseOfferingResponseDTO> enrolledAndCompletedEnrollments =
                enrollmentServiceClient.getEnrollmentsByStudent(studentID, Arrays.asList("COMPLETED", "ENROLLED"))
                        .getBody()
                        .stream()
                        .map(enrollment -> enrollment.getCourseOffering())
                        .collect(Collectors.toList());

        // Extract course codes of enrolled and completed courses
        Set<String> enrolledAndCompletedCourseCodes = enrolledAndCompletedEnrollments.stream()
                .map(enrollment -> enrollment.getCourse().getCode())
                .collect(Collectors.toSet());

        // Filter out course offerings that the student has already enrolled in or completed
        List<CourseOffering> unEnrolledCourseOfferings = allCourseOfferings.stream()
                .filter(courseOffering -> !enrolledAndCompletedCourseCodes.contains(courseOffering.getCourse().getCode()))
                .collect(Collectors.toList());

        // List to store eligible course offerings
        List<CourseOfferingResponseDTO> eligibleCourseOfferings = new ArrayList<>();

        for (CourseOffering courseOffering : unEnrolledCourseOfferings) {
            // Check if the student has an approved special enrollment request for this course offering
            EnrollmentRequestResponseDTO specialEnrollmentRequest = null;

            try {
                ResponseEntity<EnrollmentRequestResponseDTO> response =
                        enrollmentServiceClient.getRequestByStudentIdAndOfferingId((long) studentID, courseOffering.getOfferingID());

                if (response.getStatusCode().is2xxSuccessful()) { // Ensure it's a successful response
                    specialEnrollmentRequest = response.getBody();
                }
            } catch (FeignException.NotFound e) {
                // Handle 404 response gracefully
                System.out.println("Enrollment request not found for student " + studentID + " and offering " + courseOffering.getOfferingID());
            } catch (FeignException e) {
                // Handle other Feign client errors
                System.out.println("Feign error: " + e.status() + " - " + e.getMessage());
                throw e; // Re-throw for other HTTP errors
            } catch (Exception e) {
                // Catch any other unexpected exceptions
                System.out.println("Unexpected error: " + e.getClass().getName());
                e.printStackTrace();
            }

            if (specialEnrollmentRequest != null &&
                    specialEnrollmentRequest.getApprovalStatus() == EnrollmentRequestResponseDTO.ApprovalStatus.APPROVED) {
                // If approved, add the course offering to the eligible list
                eligibleCourseOfferings.add(mapToCourseOfferingResponseDTO(courseOffering));
            }

            // If no approved special enrollment request, check prerequisites
            List<PrerequisiteDTO> prerequisites = courseService.getPrerequisitesById(courseOffering.getCourse().getCourseID());

            // Check if all prerequisites are completed
            boolean allPrerequisitesCompleted = prerequisites.size() == 0 || prerequisites.stream()
                    .allMatch(prerequisite -> enrolledAndCompletedCourseCodes.contains(prerequisite.getCode()));

            if (allPrerequisitesCompleted) {
                // If prerequisites are met, add the course offering to the eligible list
                eligibleCourseOfferings.add(mapToCourseOfferingResponseDTO(courseOffering));
            }
        }

        return eligibleCourseOfferings;
    }
    public List<CourseOfferingResponseDTO> getCurrentCourseOfferings() {
        String currentTermCode = termService.getCurrentTermCode();
        return courseOfferingRepository.findByTermCode(currentTermCode).stream()
                .map(this::mapToCourseOfferingResponseDTO)
                .collect(Collectors.toList());
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

    public List<CourseOfferingResponseDTO> getCourseOfferingsByCourseId(Long courseId) {
        return courseOfferingRepository.searchCourseOfferingsByCourseID(courseId).stream()
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