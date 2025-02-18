package com.project.enrollmentservice.feignclient.client;

import com.project.enrollmentservice.feignclient.dto.CourseOfferingResponseDTO;
import com.project.enrollmentservice.feignclient.dto.CourseResponseDTO;
import com.project.enrollmentservice.feignclient.dto.TermResponseDTO;
import com.project.enrollmentservice.feignclient.fallback.CurriculumServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "curriculum-service", fallback = CurriculumServiceFallback.class)
public interface CurriculumServiceClient {
    @GetMapping("/demo/hi")
    String hi();
    @GetMapping("/api/course-offerings/{offeringId}")
    ResponseEntity<CourseOfferingResponseDTO> getCourseOfferingById(
            @PathVariable Long offeringId);
    @GetMapping("/api/course-offerings/search/course_id/{courseId}")
    ResponseEntity<List<CourseOfferingResponseDTO>> getCourseOfferingsByCourseId(
            @PathVariable Long courseId);
    @PutMapping("/api/course-offerings/{offeringId}/decrement-seats")
    ResponseEntity<Void> decrementAvailableSeats(@PathVariable Long offeringId);
    @GetMapping("/api/courses/{courseId}")
    ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long courseId);
    @GetMapping("/api/terms/{termId}")
    ResponseEntity<TermResponseDTO> getTermById(@PathVariable Long termId);
    @GetMapping("/api/terms/current")
    ResponseEntity<TermResponseDTO> getCurrentTerm();

}