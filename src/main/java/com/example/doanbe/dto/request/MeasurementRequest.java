package com.example.doanbe.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class MeasurementRequest {
    private String measurementId;
    private List<Record> records;

    @Data
    public static class Record {
        private int blockIndex;
        private int plotIndex;
        private String treatmentCode;
        private List<Value> values;
    }

    @Data
    public static class Value {
        private String criterionCode;
        private String criterionName;
        private double value;
    }
}
