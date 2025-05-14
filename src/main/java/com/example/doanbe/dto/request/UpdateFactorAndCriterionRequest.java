package com.example.doanbe.dto.request;

import com.example.doanbe.document.Plot;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class UpdateFactorAndCriterionRequest {
    private String factorId;
    private String factorCode;
    private String factorName;
    private List<LevelRequest> levels;
    private List<CriterionRequest> criteria;


    private String experimentType;
    private int blocks;
    private int replicates;
    private int columns;
    private List<List<Plot>> layout;

    @Data
    @Getter
    @Builder
    public static class LevelRequest {
        private String id;
        private String levelCode;
        private String levelName;
    }
}



