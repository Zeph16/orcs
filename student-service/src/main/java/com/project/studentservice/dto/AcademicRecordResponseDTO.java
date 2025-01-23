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
public class AcademicRecordResponseDTO implements Serializable {
    private Integer id;
    private String studentCardId;
    private String courseCode;
    private String courseName;
    private Integer termId;
    private Grade grade;
}