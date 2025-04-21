package com.example.doanbe.dto;

import lombok.Data;

@Data
public class VerifyOTPRequestDto {
    private String email;
    private String otp;
}