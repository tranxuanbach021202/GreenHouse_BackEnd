package com.example.doanbe.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PresignedUrlRequest {
    @NotBlank(message = "File name is required")
    private String fileName;

    @NotBlank(message = "Content type is required")
    private String contentType;

    private Long expirationTime;

    private String folder;
}


