package com.example.doanbe.payload.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class QrGenerateRequest {
    private String projectId;
    private String urlApp;
}

