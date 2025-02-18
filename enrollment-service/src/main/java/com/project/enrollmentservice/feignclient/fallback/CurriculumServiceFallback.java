package com.project.enrollmentservice.feignclient.fallback;

import com.project.enrollmentservice.feignclient.client.CurriculumServiceClient;
import com.project.enrollmentservice.feignclient.dto.CourseOfferingResponseDTO;
import com.project.enrollmentservice.feignclient.dto.CourseResponseDTO;
import com.project.enrollmentservice.feignclient.dto.TermResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public class CurriculumServiceFallback implements CurriculumServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach curriculum-service /demo/hi endpoint!";
    }
    @Override
    public ResponseEntity<CourseOfferingResponseDTO> getCourseOfferingById(Long offeringId) {
        CourseOfferingResponseDTO fallbackResponse = new CourseOfferingResponseDTO();
        fallbackResponse.setInstructorName("ERROR: THIS IS FALLBACK RESPONSE");
        return ResponseEntity.ok(fallbackResponse);
    }

    @Override
    public ResponseEntity<List<CourseOfferingResponseDTO>> getCourseOfferingsByCourseId(Long courseId) {
        return null;
    }

    @Override
    public ResponseEntity<Void> decrementAvailableSeats(Long offeringId) {
        return null;
    }

    @Override
    public ResponseEntity<CourseResponseDTO> getCourseById(Long courseId) {
        return null;
    }

    @Override
    public ResponseEntity<TermResponseDTO> getTermById(Long termId) {
        return null;
    }

    @Override
    public ResponseEntity<TermResponseDTO> getCurrentTerm() {
        return null;
    }
}