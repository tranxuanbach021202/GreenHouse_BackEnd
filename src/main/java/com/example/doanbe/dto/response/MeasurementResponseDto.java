package com.example.doanbe.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class MeasurementResponseDto {
    private String id;
    private String projectId;
    private String userId;
    private LocalDate start;
    private LocalDate end;
    private LocalDateTime createdAt;
}
