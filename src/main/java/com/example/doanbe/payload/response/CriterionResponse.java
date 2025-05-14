package com.example.doanbe.payload.response;
import lombok.Data;

import java.util.Date;

@Data
public class CriterionResponse {
    private String id;
    private String criterionCode;
    private String criterionName;
    private Date createdAt;
    private Date updatedAt;
}
