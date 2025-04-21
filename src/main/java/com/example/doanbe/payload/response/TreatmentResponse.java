package com.example.doanbe.payload.response;

import lombok.Data;

import java.util.Date;

@Data
public class TreatmentResponse {
    private String id;
    private String treatmentCode;
    private String treatmentName;
    private String factorId;
    private String levelId;
    private Date createdAt;
    private Date updatedAt;
}
