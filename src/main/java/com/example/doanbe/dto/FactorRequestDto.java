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
public class FactorRequestDto {
    @NotBlank(message = "Factor code is required")
    private String code;

    @NotBlank(message = "Factor name is required")
    private String name;

    @NotEmpty(message = "Levels cannot be empty")
    private List<LevelRequestDto> levels;
}
