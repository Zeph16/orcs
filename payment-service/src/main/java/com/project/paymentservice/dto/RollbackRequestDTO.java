package com.project.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RollbackRequestDTO {
    @NotNull(message = "Payment ID is needed")
    private Long paymentId;
    
    @NotBlank(message = "Rollback reason is needed")
    private String reason;
}
