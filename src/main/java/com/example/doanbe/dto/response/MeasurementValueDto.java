package com.example.doanbe.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeasurementValueDto {
    private String criterionCode;
    private String criterionName;
    private String value;
    private int blockIndex;
    private int plotIndex;
}
