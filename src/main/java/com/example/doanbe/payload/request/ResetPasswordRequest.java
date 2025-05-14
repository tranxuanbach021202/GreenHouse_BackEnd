package com.example.doanbe.payload.request;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String resetToken;
    private String newPassword;
}
