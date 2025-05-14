package com.example.doanbe.dto.request;

import lombok.Data;

import java.util.Date;

@Data
public class UpdateProjectRequest {
    private String projectName;
    private String projectCode;
    private String thumbnailUrl;
    private Date startDate;
    private Date endDate;
    private String description;
}

