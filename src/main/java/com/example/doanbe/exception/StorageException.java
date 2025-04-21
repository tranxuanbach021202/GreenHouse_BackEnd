package com.example.doanbe.exception;

import lombok.Getter;

@Getter
public class StorageException extends RuntimeException {
    private final String code;

    public StorageException(String message, String code) {
        super(message);
        this.code = code;
    }
}