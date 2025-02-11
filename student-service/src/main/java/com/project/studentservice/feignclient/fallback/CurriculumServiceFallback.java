package com.project.studentservice.feignclient.fallback;

import com.project.studentservice.feignclient.client.CurriculumServiceClient;
import com.project.studentservice.feignclient.dto.CourseResponseDTO;
import com.project.studentservice.feignclient.dto.CourseType;
import com.project.studentservice.feignclient.dto.DepartmentResponseDTO;
import com.project.studentservice.feignclient.dto.ProgramResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

public class CurriculumServiceFallback implements CurriculumServiceClient {

    @Override
    public String hi() {
        return "Oops, can't reach curriculum-service /demo/hi endpoint!";
    }


    @Override
    public ResponseEntity<CourseResponseDTO> getCourseById(Long courseId) {
        CourseResponseDTO courseResponseDTO = CourseResponseDTO.builder()
                .courseId(1L)
                .title("fallback course")
                .code("fbcrs")
                .creditHrs(0)
                .type(CourseType.MAJOR)
                .build();
        return ResponseEntity.ok(courseResponseDTO);
    }

    @Override
    public ResponseEntity<ProgramResponseDTO> getProgramById(Long id) {
        ProgramResponseDTO programResponseDTO = ProgramResponseDTO.builder()
                .programId(id)
                .code("fbprog")
                .departments(Collections.emptyList())
                .name("fallback program")
                .build();
        return ResponseEntity.ok(programResponseDTO);
    }

    @Override
    public ResponseEntity<DepartmentResponseDTO> getDepartmentById(Long id) {
        DepartmentResponseDTO departmentResponseDTO = DepartmentResponseDTO.builder()
                .departmentId(id)
                .programs(Collections.emptyList())
                .name("fallback department")
                .head("fallback head")
                .code("fbdep")
                .build();
        return ResponseEntity.ok(departmentResponseDTO);
    }
}