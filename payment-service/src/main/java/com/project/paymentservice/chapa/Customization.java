package com.project.paymentservice.chapa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Customization {
    private String title;
    private String description;
}