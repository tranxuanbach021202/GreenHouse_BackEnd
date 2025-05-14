package com.example.doanbe.payload.response;

import com.example.doanbe.document.Criterion;
import com.example.doanbe.document.Factor;
import com.example.doanbe.document.Plot;
import com.example.doanbe.document.Treatment;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
public class ProjectDetailResponse {
    private String id;
    private String code;
    private String name;
    private Date startDate;
    private Date endDate;
    private String description;
    private String thumbnailUrl;
    private List<ProjectMemberResponse> members;
    private Factor factor;
    private List<Criterion> criteria;
    private List<Treatment> treatments;
    private String experimentType;
    private int blocks;
    private int replicates;
    private int columns;
    private List<List<Plot>> layout;
    @JsonProperty("isPublic")
    private boolean publicVisible;
}

