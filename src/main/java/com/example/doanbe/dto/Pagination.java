package com.example.doanbe.dto;

import lombok.Data;

@Data
public class Pagination {
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean hasNext;
    private Boolean hasPrevious;
}