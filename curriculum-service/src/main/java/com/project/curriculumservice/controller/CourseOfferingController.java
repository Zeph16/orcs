package com.project.curriculumservice.controller;

import com.project.curriculumservice.dto.CourseOfferingRequestDTO;
import com.project.curriculumservice.dto.CourseOfferingResponseDTO;
import com.project.curriculumservice.service.CourseOfferingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/course-offerings")
@RequiredArgsConstructor
public class CourseOfferingController {
    private final CourseOfferingService courseOfferingService;

    // TODO: Add decrement capacity method for each enrollment on a given offering

    @PostMapping
    public ResponseEntity<CourseOfferingResponseDTO> createCourseOffering(
            @RequestBody CourseOfferingRequestDTO requestDTO) {
        return new ResponseEntity<>(
                courseOfferingService.createCourseOffering(requestDTO),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{offeringId}")
    public ResponseEntity<CourseOfferingResponseDTO> updateCourseOffering(
            @PathVariable Long offeringId,
            @RequestBody CourseOfferingRequestDTO requestDTO) {
        return ResponseEntity.ok(
                courseOfferingService.updateCourseOffering(offeringId, requestDTO)
        );
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<CourseOfferingResponseDTO>>  getCourseOfferingsForStudent(@PathVariable int studentId) {
        return ResponseEntity.ok(
                courseOfferingService.getCourseOfferingsForStudent(studentId)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<CourseOfferingResponseDTO>> searchCourseOfferings(
            @RequestParam String query) {
        return ResponseEntity.ok(
                courseOfferingService.searchCourseOfferings(query)
        );
    }

    @GetMapping("/{offeringId}")
    public ResponseEntity<CourseOfferingResponseDTO> getCourseOfferingById(
            @PathVariable Long offeringId) {
        return ResponseEntity.ok(
                courseOfferingService.getCourseOfferingById(offeringId)
        );
    }
    @GetMapping("/search/batch_id/{batchId}")
    public ResponseEntity<List<CourseOfferingResponseDTO>> getCourseOfferingByBatchId(
            @PathVariable Long batchId) {
        return ResponseEntity.ok(
                courseOfferingService.getCourseOfferingsByBatchId(batchId)
        );
    }

    @GetMapping("/search/course_id/{courseId}")
    public ResponseEntity<List<CourseOfferingResponseDTO>> getCourseOfferingsByCourseId(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(
                courseOfferingService.getCourseOfferingsByCourseId(courseId)
        );
    }

    @GetMapping("/current")
    public ResponseEntity<List<CourseOfferingResponseDTO>> getCurrentCourseOfferings() {
        return ResponseEntity.ok(courseOfferingService.getCurrentCourseOfferings());
    }
    @GetMapping
    public ResponseEntity<List<CourseOfferingResponseDTO>> getAllCourseOfferings() {
        return ResponseEntity.ok(
                courseOfferingService.getAllCourseOfferings()
        );
    }

    @DeleteMapping("/{offeringId}")
    public ResponseEntity<Void> deleteCourseOffering(@PathVariable Long offeringId) {
        courseOfferingService.deleteCourseOffering(offeringId);
        return ResponseEntity.noContent().build();
    }
}