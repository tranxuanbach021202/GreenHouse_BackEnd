package com.example.doanbe.dto.response;

import com.example.doanbe.document.Criterion;
import com.example.doanbe.dto.UserDto;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Setter
@NoArgsConstructor
public class MeasurementDetailDto {
    private String id;
    private String projectId;
    private String userId;
    private int columnInBlock;
    private List<Criterion> criterionList;
    private UserDto createdBy;
    private LocalDate start;
    private LocalDate end;
    private LocalDateTime createdAt;
    private List<RecordDTO> records;

    @Data
    @NoArgsConstructor
    public static class RecordDTO {
        private int blockIndex;
        private int plotIndex;
        private String treatmentCode;
        private List<ValueDTO> values;
    }

    @Data
    @NoArgsConstructor
    public static class ValueDTO {
        private String criterionCode;
        private String criterionName;
        private double value;
    }
}

