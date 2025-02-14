package com.project.enrollmentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "special_enrollment_requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpecialEnrollmentRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long requestID;

    @Column(nullable = false)
    private Long studentID;

    @Column(nullable = false)
    private Long offeringID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestType requestType;

    @Column(nullable = false, length = 1000)
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalStatus approvalStatus;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    public enum RequestType {
        ADD, REGISTRATION
    }

    public enum ApprovalStatus {
        PENDING, APPROVED, REJECTED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
