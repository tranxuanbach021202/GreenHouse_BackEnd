package com.example.doanbe.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class ApiErrorResponseDto {
    private  String code;
    private String title;
    private String message;

    public ApiErrorResponseDto(int i, String khôngTìmThấyToken) {
    }
}
