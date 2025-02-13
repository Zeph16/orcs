package com.project.enrollmentservice.repository;

import com.project.enrollmentservice.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudentID(int studentID);
    List<Enrollment> findByOfferingID(Long offeringID);
    List<Enrollment> findByStatus(Enrollment.EnrollmentStatus status);
    List<Enrollment> findByEnrollmentDateBetween(LocalDate startDate, LocalDate endDate);
    List<Enrollment> findByStudentIDAndStatusIn(int studentID, List<Enrollment.EnrollmentStatus> statuses);
}