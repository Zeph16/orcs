package com.project.curriculumservice.dto;

import com.project.curriculumservice.feignclient.dtos.BatchResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentResponseDTO implements Serializable {
    private Integer id;
    private String name;
    private String cardId;
    private String email;
    private String phone;
    private String address;
    private String enrollmentStatus;
    private BatchResponseDTO batch;
}
