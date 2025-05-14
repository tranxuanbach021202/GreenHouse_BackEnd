package com.example.doanbe.payload.response;

import com.example.doanbe.dto.Pagination;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SuccessResponse {
    private int code = 0;
    private String message = "Thành công!";
    private Object data;
    private Pagination meta;

    public SuccessResponse(Object data, Pagination meta) {
        this.data = data;
        this.meta = meta;
    }

    public SuccessResponse(Object data) {
        this.data = data;
        this.meta = null;
    }
}