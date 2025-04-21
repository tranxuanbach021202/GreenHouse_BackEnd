package com.example.doanbe.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserPagingRequest {
    @Min(0)
    @Builder.Default
    private Integer page = 0;

    @Min(1)
    @Max(100)
    @Builder.Default
    private Integer size = 20;

    @Builder.Default
    private String sortBy = "id";

    @Builder.Default
    private String sortDir = "asc";
    private String search;
}