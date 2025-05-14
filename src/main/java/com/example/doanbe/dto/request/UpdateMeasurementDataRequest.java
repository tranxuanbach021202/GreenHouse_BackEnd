package com.example.doanbe.dto.request;

import com.example.doanbe.document.Measurement;
import lombok.Data;
import java.util.List;

@Data
public class UpdateMeasurementDataRequest {
    private List<Measurement.Record> records;

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

