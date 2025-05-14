package com.example.doanbe.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String displayName;
    private String urlAvatar;
    private String bio;
}

