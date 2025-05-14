package com.example.doanbe.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "measurement")
public class Measurement {
    @Id
    private String id;
    private String projectId;
    private String userId;
    private LocalDate start;
    private LocalDate end;
    private LocalDateTime createdAt;
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
