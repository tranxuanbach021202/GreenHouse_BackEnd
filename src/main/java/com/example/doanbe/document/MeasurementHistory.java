package com.example.doanbe.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "measurement_history")
public class MeasurementHistory {
    @Id
    private String id;
    private String measurementId;
    private int blockIndex;
    private int plotIndex;
    private String criterionCode;
    private String criterionName;
    private double oldValue;
    private double newValue;
    private String userId;
    private String action;
    private LocalDateTime timestamp;

    private String editSessionId;
}

