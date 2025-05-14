package com.example.doanbe.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateMeasurementRequest {
    private LocalDate start;
    private LocalDate end;
}

