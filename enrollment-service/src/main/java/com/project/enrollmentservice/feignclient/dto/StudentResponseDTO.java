package com.project.enrollmentservice.feignclient.dto;

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
    private Long id;
    private String name;
    private String cardId;
    private String email;
    private String phone;
    private String address;
    private String enrollmentStatus;
    private BatchResponseDTO batch;
}