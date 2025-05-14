package com.example.doanbe.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MeasurementHistoryResponse {
    private int blockIndex;
    private int plotIndex;
    private String criterionCode;
    private String criterionName;
    private double oldValue;
    private double newValue;
    private String action;
    private LocalDateTime timestamp;

    private String userId;
    private String username;
    private String urlAvatar;
}

