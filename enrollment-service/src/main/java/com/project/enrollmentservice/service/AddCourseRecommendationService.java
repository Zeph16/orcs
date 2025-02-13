package com.project.enrollmentservice.service;

import com.project.enrollmentservice.dto.AddCourseRecommendationRequest;
import com.project.enrollmentservice.dto.AddCourseRecommendationResponse;
import com.project.enrollmentservice.dto.EligibleStudentsDTO;
import com.project.enrollmentservice.dto.EnrollmentResponseDTO;
import com.project.enrollmentservice.exception.ResourceNotFoundException;
import com.project.enrollmentservice.feignclient.client.CurriculumServiceClient;
import com.project.enrollmentservice.feignclient.client.StudentServiceClient;
import com.project.enrollmentservice.feignclient.dto.CourseResponseDTO;
import com.project.enrollmentservice.feignclient.dto.StudentResponseDTO;
import com.project.enrollmentservice.feignclient.dto.TermResponseDTO;
import com.project.enrollmentservice.model.AddCourseRecommendation;
import com.project.enrollmentservice.model.Enrollment;
import com.project.enrollmentservice.repository.AddCourseRecommendationRepository;
import com.project.enrollmentservice.service.AddCourseRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddCourseRecommendationService {
    private final AddCourseRecommendationRepository recommendationRepository;
    private final StudentServiceClient studentClient;
    private final CurriculumServiceClient curriculumClient;
    private final EnrollmentService enrollmentService;

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

        return buildResponse(savedRecommendation, student, term, course);
    }

    public List<EligibleStudentsDTO> getEligibleStudentsForCourse(Long courseId) {
        // Get the current term
        TermResponseDTO currentTerm = curriculumClient.getCurrentTerm().getBody();
        String currentTermCode = currentTerm.getCode();
        Long currentTermId = currentTerm.getTermId();

        // Fetch enrollments for the course
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);

        // Filter enrollments based on status and term code
        List<Integer> studentIds = enrollments.stream()
                .filter(enrollment -> enrollment.getStatus() == Enrollment.EnrollmentStatus.NOT_COMPLETED)
                .filter(enrollment -> !enrollment.getCourseOffering().getTerm().getCode().equals(currentTermCode))
                .map(EnrollmentResponseDTO::getStudentID)
                .distinct() // Avoid duplicate student IDs
                .collect(Collectors.toList());

        // Fetch student details, their enrollments, and recommendations, then map to EligibleStudentsDTO
        return studentIds.stream()
                .map(studentId -> {
                    // Fetch the student details
                    StudentResponseDTO student = studentClient.getStudentById(studentId).getBody();

                    // Fetch all enrollments for the student
                    List<EnrollmentResponseDTO> studentEnrollments = enrollmentService.getEnrollmentsByStudent(studentId);

                    // Filter enrollments with status ENROLLED for the current term
                    List<EnrollmentResponseDTO> enrolledCourses = studentEnrollments.stream()
                            .filter(enrollment -> enrollment.getCourseOffering().getTerm().getCode().equals(currentTermCode))
                            .filter(enrollment -> enrollment.getStatus() == Enrollment.EnrollmentStatus.ENROLLED)
                            .collect(Collectors.toList());

                    // Count the number of courses of each type (courses taking)
                    int majorCoursesTaking = (int) enrolledCourses.stream()
                            .filter(enrollment -> enrollment.getCourseOffering().getCourse().getType() == CourseResponseDTO.CourseType.MAJOR)
                            .count();

                    int commonCoursesTaking = (int) enrolledCourses.stream()
                            .filter(enrollment -> enrollment.getCourseOffering().getCourse().getType() == CourseResponseDTO.CourseType.COMMON)
                            .count();

                    int electiveCoursesTaking = (int) enrolledCourses.stream()
                            .filter(enrollment -> enrollment.getCourseOffering().getCourse().getType() == CourseResponseDTO.CourseType.ELECTIVE)
                            .count();

                    // Fetch recommended courses for the student and current term
                    List<AddCourseRecommendation> recommendedCourses = recommendationRepository.findByStudentIDAndTermID(studentId, currentTermId);

                    // Count the number of recommended courses of each type
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

                    // Build the EligibleStudentsDTO
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
                .filter(eligibleStudent -> eligibleStudent.getStudent() != null) // Ensure student is not null
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

    public List<AddCourseRecommendationResponse> getRecommendationsByStudent(Integer studentId) {
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
