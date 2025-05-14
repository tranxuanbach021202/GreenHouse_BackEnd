package com.example.doanbe.payload.request;

import com.example.doanbe.dto.CriterionRequestDto;
import com.example.doanbe.dto.FactorRequestDto;
import com.example.doanbe.dto.ProjectMemberRequestDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class CreateProjectRequest {
    @NotBlank(message = "Project code is required")
    private String projectCode;

    @NotBlank(message = "Project name is required")
    private String projectName;

    @NotNull(message = "Start date is required")
    private Date startDate;

    @NotNull(message = "End date is required")
    private Date endDate;
    private String description;
    private String thumbnailUrl;
    private List<ProjectMemberRequestDto> members;
    private FactorRequestDto factor;
    private List<CriterionRequestDto> criteria;
    @Min(value = 1, message = "Blocks must be at least 1")
    private int blocks;

    @Min(value = 1, message = "Replicates must be at least 1")
    private int replicates;

    @Min(value = 1, message = "Columns must be at least 1")
    private int columns;
    @NotNull(message = "Experiment type is required")
    private String experimentType;
    private List<List<String>> layout;
}
