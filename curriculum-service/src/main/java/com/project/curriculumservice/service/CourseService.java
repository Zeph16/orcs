package com.project.curriculumservice.service;

import com.project.curriculumservice.dto.CourseRequestDTO;
import com.project.curriculumservice.dto.CourseResponseDTO;
import com.project.curriculumservice.dto.DepartmentProgramResponseDTO;
import com.project.curriculumservice.dto.PrerequisiteDTO;
import com.project.curriculumservice.exception.ResourceNotFoundException;
import com.project.curriculumservice.model.Course;
import com.project.curriculumservice.model.DepartmentProgram;
import com.project.curriculumservice.repository.CourseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {
    private final CourseRepository courseRepository;
    private final DepartmentProgramService departmentProgramService;

    public CourseResponseDTO createCourse(CourseRequestDTO courseDTO) {
        if (courseDTO.getDepartmentProgramIds() == null || courseDTO.getDepartmentProgramIds().isEmpty()) {
            throw new IllegalArgumentException("At least one department-program combination must be associated with the course");
        }

        // Create the course entity
        Course course = mapToCourseEntity(courseDTO);

        // Get DepartmentProgram entities and set them
        Set<DepartmentProgram> departmentPrograms = courseDTO.getDepartmentProgramIds().stream()
                .map(dpId -> departmentProgramService.getDepartmentProgramById(dpId.getDepartmentId(), dpId.getProgramId()))
                .collect(Collectors.toSet());
        course.setDepartmentPrograms(departmentPrograms);
        if (courseDTO.getPrerequisiteIds() != null && !courseDTO.getPrerequisiteIds().isEmpty()) {
            Set<Course> prerequisites = courseDTO.getPrerequisiteIds().stream()
                    .map(id -> courseRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Prerequisite course not found with ID: " + id)))
                    .collect(Collectors.toSet());
            validatePrerequisites(course, prerequisites);
            course.setPrerequisites(prerequisites);
        }

        // Save the course
        course = courseRepository.save(course);
        return mapToCourseResponseDTO(course);
    }
    public List<CourseResponseDTO> searchCourses(String titleOrCodeQuery) {
        // Sanitize input
        String sanitizedTitleOrCodeQuery = sanitizeSearchTerm(titleOrCodeQuery);

        // Perform search
        List<Course> courses = courseRepository.searchCourses(sanitizedTitleOrCodeQuery);

        // Map results to DTOs
        return courses.stream()
                .map(this::mapToCourseResponseDTO)
                .collect(Collectors.toList());
    }

    public CourseResponseDTO getCourseById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        return mapToCourseResponseDTO(course);
    }

    public List<PrerequisiteDTO> getPrerequisitesById(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        // Map prerequisites to PrerequisiteDTO
        List<PrerequisiteDTO> prerequisites = course.getPrerequisites().stream()
                .map(prerequisite -> PrerequisiteDTO.builder()
                        .courseId(prerequisite.getCourseID())
                        .title(prerequisite.getTitle())
                        .code(prerequisite.getCode())
                        .build())
                .collect(Collectors.toList());
        return prerequisites;
    }

    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseResponseDTO)
                .collect(Collectors.toList());
    }

    public CourseResponseDTO updateCourse(Long courseId, CourseRequestDTO courseDTO) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));

        // Update basic fields
        existingCourse.setTitle(courseDTO.getTitle());
        existingCourse.setCode(courseDTO.getCode());
        existingCourse.setCreditHrs(courseDTO.getCreditHrs());
        existingCourse.setType(courseDTO.getType());
        existingCourse.setStatus(courseDTO.getStatus());
        existingCourse.setDescription(courseDTO.getDescription());

        // Update course objectives
        if (courseDTO.getCourseObjectives() != null) {
            updateElementCollection(
                    existingCourse.getCourseObjectives(),
                    courseDTO.getCourseObjectives()
            );
        }

        // Update course content
        if (courseDTO.getCourseContent() != null) {
            updateElementCollection(
                    existingCourse.getCourseContent(),
                    courseDTO.getCourseContent()
            );
        }

        // Update department-program associations if provided
        if (courseDTO.getDepartmentProgramIds() != null && !courseDTO.getDepartmentProgramIds().isEmpty()) {
            Set<DepartmentProgram> departmentPrograms = courseDTO.getDepartmentProgramIds().stream()
                    .map(dpId -> departmentProgramService.getDepartmentProgramById(dpId.getDepartmentId(), dpId.getProgramId()))
                    .collect(Collectors.toSet());
            existingCourse.setDepartmentPrograms(departmentPrograms);
        }
        // Update prerequisites
        if (courseDTO.getPrerequisiteIds() != null) {
            Set<Course> prerequisites = courseDTO.getPrerequisiteIds().stream()
                    .map(id -> courseRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException("Prerequisite course not found with ID: " + id)))
                    .collect(Collectors.toSet());
            validatePrerequisites(existingCourse, prerequisites);
            existingCourse.setPrerequisites(prerequisites);
        }

        Course updatedCourse = courseRepository.save(existingCourse);
        return mapToCourseResponseDTO(updatedCourse);
    }
    private <T> void updateElementCollection(List<T> existingCollection, List<T> newItems) {
        existingCollection.clear();
        if (newItems != null && !newItems.isEmpty()) {
            existingCollection.addAll(newItems);
        }
    }

    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with ID: " + courseId));
        courseRepository.delete(course);
    }

    private String sanitizeSearchTerm(String term) {
        if (term == null) {
            return null;
        }
        // Remove any special characters that could be used for SQL injection
        // Keep only alphanumeric characters, spaces, and common punctuation
        return term.replaceAll("[^a-zA-Z0-9\\s\\-\\.\\,]", "").trim();
    }

    private void validatePrerequisites(Course course, Set<Course> prerequisites) {
        // Check if course is trying to be its own prerequisite
        if (prerequisites.contains(course)) {
            throw new IllegalArgumentException("A course cannot be its own prerequisite: " + course.getCode());
        }

        // Check for circular dependencies
        for (Course prerequisite : prerequisites) {
            if (hasCircularDependency(course, prerequisite, new HashSet<>())) {
                throw new IllegalArgumentException(
                        String.format("Circular prerequisite dependency detected between courses: %s and %s",
                                course.getCode(), prerequisite.getCode())
                );
            }
        }
    }

    private boolean hasCircularDependency(Course originalCourse, Course currentCourse, Set<Long> visited) {
        // Add current course to visited set
        visited.add(currentCourse.getCourseID());

        // Check prerequisites of current course
        for (Course prerequisite : currentCourse.getPrerequisites()) {
            // If we find the original course in the prerequisite chain, we have a circular dependency
            if (prerequisite.getCourseID().equals(originalCourse.getCourseID())) {
                return true;
            }

            // Skip if we've already visited this course to prevent infinite recursion
            if (visited.contains(prerequisite.getCourseID())) {
                continue;
            }

            // Recursively check prerequisites
            if (hasCircularDependency(originalCourse, prerequisite, visited)) {
                return true;
            }
        }

        return false;
    }

    public CourseResponseDTO mapToCourseResponseDTO(Course course) {
        List<DepartmentProgramResponseDTO> dpDTOs = course.getDepartmentPrograms().stream()
                .map(dp -> DepartmentProgramResponseDTO.builder()
                        .departmentId(dp.getDepartment().getDepartmentID())
                        .programId(dp.getProgram().getProgramID())
                        .departmentName(dp.getDepartment().getName())
                        .programName(dp.getProgram().getName())
                        .build())
                .collect(Collectors.toList());

        List<PrerequisiteDTO> prerequisiteDTOs = course.getPrerequisites().stream()
                .map(prereq -> PrerequisiteDTO.builder()
                        .courseId(prereq.getCourseID())
                        .title(prereq.getTitle())
                        .code(prereq.getCode())
                        .build())
                .collect(Collectors.toList());

        return CourseResponseDTO.builder()
                .courseId(course.getCourseID())
                .title(course.getTitle())
                .code(course.getCode())
                .creditHrs(course.getCreditHrs())
                .type(course.getType())
                .status(course.getStatus())
                .description(course.getDescription())
                .courseObjectives(course.getCourseObjectives())
                .courseContent(course.getCourseContent())
                .departmentPrograms(dpDTOs)
                .prerequisites(prerequisiteDTOs)
                .build();
    }

    private Course mapToCourseEntity(CourseRequestDTO courseDTO) {
        return Course.builder()
                .title(courseDTO.getTitle())
                .code(courseDTO.getCode())
                .creditHrs(courseDTO.getCreditHrs())
                .type(courseDTO.getType())
                .status(courseDTO.getStatus())
                .description(courseDTO.getDescription())
                .courseObjectives(courseDTO.getCourseObjectives() != null ?
                        new ArrayList<>(courseDTO.getCourseObjectives()) : new ArrayList<>())
                .courseContent(courseDTO.getCourseContent() != null ?
                        new ArrayList<>(courseDTO.getCourseContent()) : new ArrayList<>())
                .departmentPrograms(new HashSet<>())
                .prerequisites(new HashSet<>())
                .build();
    }

}