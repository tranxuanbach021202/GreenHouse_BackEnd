package com.example.doanbe.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriterionRequestDto {
    @NotBlank(message = "Criterion code is required")
    private String criterionCode;

    @NotBlank(message = "Criterion name is required")
    private String criterionName;

}
