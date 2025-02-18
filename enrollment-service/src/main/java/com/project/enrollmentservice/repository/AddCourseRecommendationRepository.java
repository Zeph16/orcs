package com.project.enrollmentservice.repository;

import com.project.enrollmentservice.model.AddCourseRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddCourseRecommendationRepository extends JpaRepository<AddCourseRecommendation, Long> {
    List<AddCourseRecommendation> findByStudentID(Long studentId);
    List<AddCourseRecommendation> findByTermID(Long termId);
    List<AddCourseRecommendation> findByCourseID(Long courseId);
    List<AddCourseRecommendation> findByStudentIDAndTermID(Long studentId, Long termId);

    List<AddCourseRecommendation> findByCourseIDAndTermID(Long courseId, Long currentTermId);
}

