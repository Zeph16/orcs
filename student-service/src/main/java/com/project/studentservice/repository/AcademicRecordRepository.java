package com.project.studentservice.repository;

import com.project.studentservice.model.AcademicRecord;
import com.project.studentservice.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, Integer> {
    List<AcademicRecord> findByStudent(Student student);
    List<AcademicRecord> findByCourseId(int courseId);
    List<AcademicRecord> findByTermId(int termId);
}