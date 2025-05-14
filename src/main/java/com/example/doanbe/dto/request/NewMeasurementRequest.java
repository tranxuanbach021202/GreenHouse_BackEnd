package com.example.doanbe.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class NewMeasurementRequest {
    private String projectId;
    private LocalDate start;
    private LocalDate end;
    private List<RecordDto> records;

    @Data
    public static class RecordDto {
        private int blockIndex;
        private int plotIndex;
        private String treatmentCode;
        private List<ValueDto> values;
    }

    @Data
    public static class ValueDto {
        private String criterionCode;
        private String criterionName;
        private double value;
    }
}
