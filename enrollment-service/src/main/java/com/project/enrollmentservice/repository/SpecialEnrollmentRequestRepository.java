package com.project.enrollmentservice.repository;

import com.project.enrollmentservice.model.SpecialEnrollmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SpecialEnrollmentRequestRepository extends JpaRepository<SpecialEnrollmentRequest, Long> {
    List<SpecialEnrollmentRequest> findByStudentID(Long studentID);
    List<SpecialEnrollmentRequest> findByOfferingID(Long offeringID);
    List<SpecialEnrollmentRequest> findByApprovalStatus(SpecialEnrollmentRequest.ApprovalStatus status);
    Optional<SpecialEnrollmentRequest> findByStudentIDAndOfferingID(Long studentID, Long offeringID);
}