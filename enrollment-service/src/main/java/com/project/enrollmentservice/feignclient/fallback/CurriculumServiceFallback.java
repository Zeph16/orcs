package com.project.enrollmentservice.feignclient.fallback;

import com.project.enrollmentservice.feignclient.client.CurriculumServiceClient;
import com.project.enrollmentservice.feignclient.dto.CourseOfferingResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

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
}