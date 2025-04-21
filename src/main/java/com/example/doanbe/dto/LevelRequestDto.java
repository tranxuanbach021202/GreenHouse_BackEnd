package com.example.doanbe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelRequestDto {
    @NotBlank(message = "Level code is required")
    private String code;

    @NotBlank(message = "Level name is required")
    private String name;
}
