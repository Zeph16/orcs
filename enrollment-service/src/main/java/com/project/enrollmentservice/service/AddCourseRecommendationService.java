package com.project.enrollmentservice.service;

import com.project.enrollmentservice.dto.AddCourseRecommendationRequest;
import com.project.enrollmentservice.dto.AddCourseRecommendationResponse;
import com.project.enrollmentservice.dto.EligibleStudentsDTO;
import com.project.enrollmentservice.dto.EnrollmentResponseDTO;
import com.project.enrollmentservice.exception.ResourceNotFoundException;
import com.project.enrollmentservice.feignclient.client.CurriculumServiceClient;
import com.project.enrollmentservice.feignclient.client.StudentServiceClient;
import com.project.enrollmentservice.feignclient.dto.CourseOfferingResponseDTO;
import com.project.enrollmentservice.feignclient.dto.CourseResponseDTO;
import com.project.enrollmentservice.feignclient.dto.StudentResponseDTO;
import com.project.enrollmentservice.feignclient.dto.TermResponseDTO;
import com.project.enrollmentservice.model.AddCourseRecommendation;
import com.project.enrollmentservice.model.Enrollment;
import com.project.enrollmentservice.repository.AddCourseRecommendationRepository;
import com.project.enrollmentservice.service.AddCourseRecommendationService;
import com.project.enrollmentservice.util.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddCourseRecommendationService {
    private final AddCourseRecommendationRepository recommendationRepository;
    private final StudentServiceClient studentClient;
    private final CurriculumServiceClient curriculumClient;
    private final EnrollmentService enrollmentService;
    private final EventPublisher eventPublisher;

    public AddCourseRecommendationResponse createRecommendation(AddCourseRecommendationRequest request) {
        // Verify that the referenced entities exist via FeignClients
        StudentResponseDTO student = studentClient.getStudentById(request.getStudentID()).getBody();
        TermResponseDTO term = curriculumClient.getCurrentTerm().getBody();
        CourseResponseDTO course = curriculumClient.getCourseById(request.getCourseID()).getBody();

        AddCourseRecommendation recommendation = AddCourseRecommendation.builder()
                .studentID(request.getStudentID())
                .termID(term.getTermId())
                .courseID(request.getCourseID())
                .build();

        AddCourseRecommendation savedRecommendation = recommendationRepository.save(recommendation);

        AddCourseRecommendationResponse res = buildResponse(savedRecommendation, student, term, course);

        eventPublisher.publishAddRecommendationCreated(res);

        return res;

    }

    public List<EligibleStudentsDTO> getEligibleStudentsForCourse(Long courseId) {
        // Get the current term
        TermResponseDTO currentTerm = curriculumClient.getCurrentTerm().getBody();
        String currentTermCode = currentTerm.getCode();
        Long currentTermId = currentTerm.getTermId();

        // Fetch enrollments for the course
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);

        // Group by student ID and select the latest enrollment based on enrollmentDate
        Map<Long, EnrollmentResponseDTO> latestEnrollments = enrollments.stream()
                .collect(Collectors.toMap(
                        EnrollmentResponseDTO::getStudentID, // Key: Student ID
                        Function.identity(), // Value: Enrollment object
                        (existing, replacement) -> existing.getEnrollmentDate().isAfter(replacement.getEnrollmentDate())
                                ? existing
                                : replacement // Keep the latest enrollment
                ));

        // Extract the latest enrollments as a list
        List<EnrollmentResponseDTO> filteredEnrollments = new ArrayList<>(latestEnrollments.values());

        // Apply the filtering based on status and term code
        List<Long> studentIds = filteredEnrollments.stream()
                .filter(enrollment -> enrollment.getStatus() == Enrollment.EnrollmentStatus.NOT_COMPLETED)
                .filter(enrollment -> !enrollment.getCourseOffering().getTerm().getCode().equals(currentTermCode))
                .map(EnrollmentResponseDTO::getStudentID)
                .distinct()
                .collect(Collectors.toList());

        // Get students who already have recommendations for this course in the current term
        List<AddCourseRecommendation> existingRecommendations = recommendationRepository.findByCourseIDAndTermID(courseId, currentTermId);
        List<Long> studentsWithExistingRecommendations = existingRecommendations.stream()
                .map(AddCourseRecommendation::getStudentID)
                .collect(Collectors.toList());

        // Filter out students who already have recommendations
        List<Long> eligibleStudentIds = studentIds.stream()
                .filter(studentId -> !studentsWithExistingRecommendations.contains(studentId))
                .collect(Collectors.toList());

        // Fetch student details and map to EligibleStudentsDTO
        return eligibleStudentIds.stream()
                .map(studentId -> {
                    StudentResponseDTO student = studentClient.getStudentById(studentId).getBody();
                    if (student == null) return null;

                    // Fetch all enrollments for the student
                    List<EnrollmentResponseDTO> studentEnrollments = enrollmentService.getEnrollmentsByStudent(studentId);

                    // Get only the latest enrollment per course
                    Map<Long, EnrollmentResponseDTO> latestStudentEnrollments = studentEnrollments.stream()
                            .collect(Collectors.toMap(
                                    enrollment -> enrollment.getCourseOffering().getCourse().getCourseId(),
                                    Function.identity(),
                                    (existing, replacement) -> existing.getEnrollmentDate().isAfter(replacement.getEnrollmentDate())
                                            ? existing
                                            : replacement
                            ));

                    // Filter enrollments with status ENROLLED for the current term
                    List<EnrollmentResponseDTO> enrolledCourses = latestStudentEnrollments.values().stream()
                            .filter(enrollment -> enrollment.getCourseOffering().getTerm().getCode().equals(currentTermCode))
                            .filter(enrollment -> enrollment.getStatus() == Enrollment.EnrollmentStatus.ENROLLED)
                            .collect(Collectors.toList());

                    // Count courses by type
                    int majorCoursesTaking = (int) enrolledCourses.stream()
                            .filter(enrollment -> enrollment.getCourseOffering().getCourse().getType() == CourseResponseDTO.CourseType.MAJOR)
                            .count();
                    int commonCoursesTaking = (int) enrolledCourses.stream()
                            .filter(enrollment -> enrollment.getCourseOffering().getCourse().getType() == CourseResponseDTO.CourseType.COMMON)
                            .count();
                    int electiveCoursesTaking = (int) enrolledCourses.stream()
                            .filter(enrollment -> enrollment.getCourseOffering().getCourse().getType() == CourseResponseDTO.CourseType.ELECTIVE)
                            .count();

                    // Fetch recommended courses for the student
                    List<AddCourseRecommendation> recommendedCourses = recommendationRepository.findByStudentIDAndTermID(studentId, currentTermId);
                    int majorCoursesRecommended = 0;
                    int commonCoursesRecommended = 0;
                    int electiveCoursesRecommended = 0;

                    for (AddCourseRecommendation recommendation : recommendedCourses) {
                        CourseResponseDTO course = curriculumClient.getCourseById(recommendation.getCourseID()).getBody();
                        if (course != null) {
                            switch (course.getType()) {
                                case MAJOR:
                                    majorCoursesRecommended++;
                                    break;
                                case COMMON:
                                    commonCoursesRecommended++;
                                    break;
                                case ELECTIVE:
                                    electiveCoursesRecommended++;
                                    break;
                            }
                        }
                    }

                    return EligibleStudentsDTO.builder()
                            .student(student)
                            .coursesTaking(EligibleStudentsDTO.CourseDetails.builder()
                                    .major(majorCoursesTaking)
                                    .common(commonCoursesTaking)
                                    .elective(electiveCoursesTaking)
                                    .build())
                            .coursesRecommended(EligibleStudentsDTO.CourseDetails.builder()
                                    .major(majorCoursesRecommended)
                                    .common(commonCoursesRecommended)
                                    .elective(electiveCoursesRecommended)
                                    .build())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public AddCourseRecommendationResponse getRecommendation(Long recommendationId) {
        AddCourseRecommendation recommendation = recommendationRepository.findById(recommendationId)
                .orElseThrow(() -> new ResourceNotFoundException("Recommendation not found"));

        StudentResponseDTO student = studentClient.getStudentById(recommendation.getStudentID()).getBody();
        TermResponseDTO term = curriculumClient.getTermById(recommendation.getTermID()).getBody();
        CourseResponseDTO course = curriculumClient.getCourseById(recommendation.getCourseID()).getBody();

        return buildResponse(recommendation, student, term, course);
    }

    public List<CourseOfferingResponseDTO> getRecommendedOfferingsByStudent(Long studentId) {
        String currentTermCode = curriculumClient.getCurrentTerm().getBody().getCode();

        List<AddCourseRecommendation> recommendations = recommendationRepository.findByStudentID(studentId);

        List<AddCourseRecommendation> currentTermRecommendations = recommendations.stream()
                .filter(recommendation -> {
                    String termCode = curriculumClient.getTermById(recommendation.getTermID()).getBody().getCode();
                    return termCode != null && termCode.equals(currentTermCode);
                })
                .collect(Collectors.toList());

        // Fetch course offerings for each recommended course
        List<CourseOfferingResponseDTO> recommendedOfferings = new ArrayList<>();

        for (AddCourseRecommendation recommendation : currentTermRecommendations) {
            List<CourseOfferingResponseDTO> courseOfferings = curriculumClient.getCourseOfferingsByCourseId(recommendation.getCourseID()).getBody();
            if (courseOfferings != null) {
                List<CourseOfferingResponseDTO> currentTermOfferings = courseOfferings.stream()
                        .filter(offering -> offering.getTerm().getCode().equals(currentTermCode))
                        .collect(Collectors.toList());

                recommendedOfferings.addAll(currentTermOfferings);
            }
        }

        return recommendedOfferings;
    }

    public List<AddCourseRecommendationResponse> getRecommendationsByStudent(Long studentId) {
        List<AddCourseRecommendation> recommendations = recommendationRepository.findByStudentID(studentId);
        if (recommendations.isEmpty()) {
            throw new ResourceNotFoundException("Recommendations not found for student ID: " + studentId);
        }
        return recommendations.stream()
                .map(this::buildResponseWithFeign) // Helper method (see below)
                .collect(Collectors.toList());
    }

    public List<AddCourseRecommendationResponse> getRecommendationsByTerm(Long termId) {
        List<AddCourseRecommendation> recommendations = recommendationRepository.findByTermID(termId);
        if (recommendations.isEmpty()) {
            throw new ResourceNotFoundException("Recommendations not found for term ID: " + termId);
        }
        return recommendations.stream()
                .map(this::buildResponseWithFeign) // Helper method (see below)
                .collect(Collectors.toList());
    }

    public List<AddCourseRecommendationResponse> getRecommendationsByCourse(Long courseId) {
        List<AddCourseRecommendation> recommendations = recommendationRepository.findByCourseID(courseId);
        if (recommendations.isEmpty()) {
            throw new ResourceNotFoundException("Recommendations not found for course ID: " + courseId);
        }
        return recommendations.stream()
                .map(this::buildResponseWithFeign) // Helper method (see below)
                .collect(Collectors.toList());
    }

    public void deleteRecommendation(Long recommendationId) {
        recommendationRepository.deleteById(recommendationId);
    }

    private AddCourseRecommendationResponse buildResponse(
            AddCourseRecommendation recommendation,
            StudentResponseDTO student,
            TermResponseDTO term,
            CourseResponseDTO course) {
        return AddCourseRecommendationResponse.builder()
                .recommendationID(recommendation.getRecommendationID())
                .student(student)
                .term(term)
                .course(course)
                .build();
    }

    private AddCourseRecommendationResponse buildResponseWithFeign(AddCourseRecommendation recommendation) {
        StudentResponseDTO student = studentClient.getStudentById(recommendation.getStudentID()).getBody();
        TermResponseDTO term = curriculumClient.getTermById(recommendation.getTermID()).getBody();
        CourseResponseDTO course = curriculumClient.getCourseById(recommendation.getCourseID()).getBody();
        return buildResponse(recommendation, student, term, course);
    }

}
