package com.example.doanbe.dto;

import jakarta.validation.constraints.NotBlank;

public class TokenRefreshRequestDto {
    @NotBlank
    private String refreshToken;
}
