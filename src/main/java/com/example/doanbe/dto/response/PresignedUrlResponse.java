package com.example.doanbe.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PresignedUrlResponse {
    private String presignedUrl;
    private String objectKey;
    private Long expirationTime;
}