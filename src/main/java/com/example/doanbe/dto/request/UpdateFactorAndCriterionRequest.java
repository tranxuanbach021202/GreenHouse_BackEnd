package com.example.doanbe.dto.request;

import com.example.doanbe.document.Level;
import com.example.doanbe.dto.LevelRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class UpdateFactorAndCriteriaRequest {
    private String projectId;
    private String factorId;
    private String factorCode;
    private String factorName;
    private List<LevelRequest> levels;

    private List<CriterionRequest> criteria;
}



