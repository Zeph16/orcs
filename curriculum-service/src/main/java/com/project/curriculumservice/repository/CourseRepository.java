package com.project.curriculumservice.repository;

import com.project.curriculumservice.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE " +
            "(LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(c.code) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Course> searchCourses(
            @Param("query") String titleOrCodeQuery
    );
}