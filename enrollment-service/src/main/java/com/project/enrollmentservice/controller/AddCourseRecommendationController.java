package com.project.enrollmentservice.controller;

import com.project.enrollmentservice.dto.AddCourseRecommendationRequest;
import com.project.enrollmentservice.dto.AddCourseRecommendationResponse;
import com.project.enrollmentservice.dto.EligibleStudentsDTO;
import com.project.enrollmentservice.feignclient.dto.StudentResponseDTO;
import com.project.enrollmentservice.service.AddCourseRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class AddCourseRecommendationController {
    private final AddCourseRecommendationService recommendationService;

    @PostMapping
    public ResponseEntity<AddCourseRecommendationResponse> createRecommendation(
            @RequestBody AddCourseRecommendationRequest request) {
        AddCourseRecommendationResponse response = recommendationService.createRecommendation(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{recommendationId}")
    public ResponseEntity<AddCourseRecommendationResponse> getRecommendation(
            @PathVariable Long recommendationId) {
        AddCourseRecommendationResponse response = recommendationService.getRecommendation(recommendationId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/students/{studentId}")
    public ResponseEntity<List<AddCourseRecommendationResponse>> getRecommendationsByStudent(
            @PathVariable Integer studentId) {
        List<AddCourseRecommendationResponse> responses = recommendationService.getRecommendationsByStudent(studentId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/terms/{termId}")
    public ResponseEntity<List<AddCourseRecommendationResponse>> getRecommendationsByTerm(
            @PathVariable Long termId) {
        List<AddCourseRecommendationResponse> responses = recommendationService.getRecommendationsByTerm(termId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/courses/{courseId}")
    public ResponseEntity<List<AddCourseRecommendationResponse>> getRecommendationsByCourse(
            @PathVariable Long courseId) {
        List<AddCourseRecommendationResponse> responses = recommendationService.getRecommendationsByCourse(courseId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/courses/{courseId}/eligible-students")
    public ResponseEntity<List<EligibleStudentsDTO>> getEligibleStudents(@PathVariable Long courseId) {
        List<EligibleStudentsDTO> students = recommendationService.getEligibleStudentsForCourse(courseId);

        if (students.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content
        }

        return ResponseEntity.ok(students);
    }

    @DeleteMapping("/{recommendationId}")
    public ResponseEntity<Void> deleteRecommendation(@PathVariable Long recommendationId) {
        recommendationService.deleteRecommendation(recommendationId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}