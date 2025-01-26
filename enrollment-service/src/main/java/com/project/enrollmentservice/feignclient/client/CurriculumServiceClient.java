package com.project.enrollmentservice.feignclient.client;

import com.project.enrollmentservice.feignclient.dto.CourseOfferingResponseDTO;
import com.project.enrollmentservice.feignclient.fallback.CurriculumServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "curriculum-service", fallback = CurriculumServiceFallback.class)
public interface CurriculumServiceClient {
    @GetMapping("/demo/hi")
    String hi();
    @GetMapping("/api/course-offerings/{offeringId}")
    ResponseEntity<CourseOfferingResponseDTO> getCourseOfferingById(
            @PathVariable Long offeringId);

}