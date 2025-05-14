package com.example.doanbe.document;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlotInfo {
    private int blockIndex;
    private int plotIndex;
    private String treatmentCode;
}
