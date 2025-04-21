package com.example.doanbe.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppException extends RuntimeException {

    private int httpStatusCode;
    private int code;
    private String message;
}