package com.project.studentservice.feignclient.client;

import com.project.studentservice.feignclient.dto.CourseResponseDTO;
import com.project.studentservice.feignclient.dto.DepartmentResponseDTO;
import com.project.studentservice.feignclient.dto.ProgramResponseDTO;
import com.project.studentservice.feignclient.fallback.CurriculumServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "curriculum-service", fallback = CurriculumServiceFallback.class)
public interface CurriculumServiceClient {

    @GetMapping("/demo/hi")
    String hi();

    @GetMapping("/api/courses/{courseId}")
    ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long courseId);

    @GetMapping("/api/programs/{id}")
    ResponseEntity<ProgramResponseDTO> getProgramById(@PathVariable Long id);

    @GetMapping("/api/departments/{id}")
    ResponseEntity<DepartmentResponseDTO> getDepartmentById(@PathVariable Long id);
}