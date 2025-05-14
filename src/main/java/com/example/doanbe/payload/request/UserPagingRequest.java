package com.example.doanbe.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserPagingRequest {
    @Min(0)
    private Integer page;
    @Min(1)
    @Max(100)
    private Integer size;
    private String sortBy;
    private String sortDir;
    private String search;

    public int getPage() {
        return page != null ? page : 0;
    }

    public int getSize() {
        return size != null ? size : 20;
    }

    public String getSortBy() {
        return sortBy != null ? sortBy : "id";
    }

    public String getSortDir() {
        return sortDir != null ? sortDir : "asc";
    }
}
