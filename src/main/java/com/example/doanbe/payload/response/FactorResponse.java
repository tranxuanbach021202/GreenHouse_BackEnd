package com.example.doanbe.payload.response;

import com.example.doanbe.document.Level;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class FactorResponse {
    private String id;
    private String factorCode;
    private String factorName;
    private List<Level> levels;
    private Date createdAt;
    private Date updatedAt;
}
