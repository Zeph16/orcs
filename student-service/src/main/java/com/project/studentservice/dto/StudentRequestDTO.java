package com.project.studentservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentRequestDTO {
    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "Card ID cannot be empty")
    private String cardId;

    @Email(message = "Invalid email format")
    private String email;

    @NotEmpty(message = "Phone number cannot be empty")
    private String phone;

    @NotEmpty(message = "Address cannot be empty")
    private String address;

    @NotNull(message = "Enrollment status is required")
    private String enrollmentStatus;

    private Long batchId;
}