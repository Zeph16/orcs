package com.project.studentservice.dto;

import com.project.studentservice.model.Grade;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcademicRecordResponseDTO {
    private int id;
    private String studentCardId;
    private String courseCode;
    private String courseName;
    private int termId;
    private Float midExam; // New field
    private Float finalExam; // New field
    private Float assignment; // New field
    private Float lab; // New field for lab marks
    private Grade grade;
}