package com.example.doanbe.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {
    private String id;
    private String projectCode;
    private String projectName;
    private Date startDate;
    private Date endDate;
    private String description;
    private String thumbnailUrl;
    private String experimentType;
    private List<List<String>> layout;
    private Date createdAt;
    private Date updatedAt;
}
