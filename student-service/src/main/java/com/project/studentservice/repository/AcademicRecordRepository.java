package com.project.studentservice.repository;

import com.project.studentservice.model.AcademicRecord;
import com.project.studentservice.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, Long> {
    List<AcademicRecord> findByStudent(Student student);
    List<AcademicRecord> findByCourseId(Long courseId);
    List<AcademicRecord> findByTermId(Long termId);
    @Query("SELECT rec FROM AcademicRecord rec WHERE " +
            "LOWER(rec.student.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(rec.student.cardId) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<AcademicRecord> searchAcademicRecords(@Param("query")String query);
}