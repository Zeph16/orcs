package com.project.enrollmentservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "add_course_recommendations")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddCourseRecommendation {
    @Id
    @GeneratedValue
    private Long recommendationID;

    @Column(nullable = false)
    private Long studentID;

    @Column(nullable = false)
    private Long termID;

    @Column(nullable = false)
    private Long courseID;
}
