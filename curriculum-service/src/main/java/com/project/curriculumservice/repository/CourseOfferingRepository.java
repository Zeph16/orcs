package com.project.curriculumservice.repository;

import com.project.curriculumservice.model.CourseOffering;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseOfferingRepository extends JpaRepository<CourseOffering, Long> {
    @Query("SELECT co FROM CourseOffering co WHERE " +
            "LOWER(co.course.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(co.course.code) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(co.instructorName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<CourseOffering> searchCourseOfferings(@Param("query") String query);

    List<CourseOffering> searchCourseOfferingsByBatchID(@Param("query") Long batchID);
    @Query("SELECT co FROM CourseOffering co " +
            "JOIN co.course c " +
            "JOIN c.departmentPrograms dp " +
            "JOIN co.term t " +
            "WHERE dp.department.departmentID = :departmentId " +
            "AND dp.program.programID = :programId " +
            "AND t.code = :termCode")
    List<CourseOffering> findByDepartmentIdAndProgramIdAndTermCode(
            @Param("departmentId") Long departmentId,
            @Param("programId") Long programId,
            @Param("termCode") String termCode);
}
