package com.example.doanbe.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EditSessionHistoryResponse {
    private String editSessionId;
    private LocalDateTime timestamp;
    private String userId;
    private String username;
    private String urlAvatar;
    private List<ChangeDetail> changes;

    @Data
    public static class ChangeDetail {
        private int blockIndex;
        private int plotIndex;
        private String criterionCode;
        private String criterionName;
        private double oldValue;
        private double newValue;
    }
}
