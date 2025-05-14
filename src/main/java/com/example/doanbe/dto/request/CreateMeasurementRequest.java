package com.example.doanbe.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMeasurementRequest {
    private String projectId;
    private LocalDate start;
    private LocalDate end;
}
