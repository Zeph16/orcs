package com.project.enrollmentservice.repository;

import com.project.enrollmentservice.model.AddCourseRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddCourseRecommendationRepository extends JpaRepository<AddCourseRecommendation, Long> {
    List<AddCourseRecommendation> findByStudentID(Integer studentId);
    List<AddCourseRecommendation> findByTermID(Long termId);
    List<AddCourseRecommendation> findByCourseID(Long courseId);
    List<AddCourseRecommendation> findByStudentIDAndTermID(int studentId, Long termId);
}

