package com.example.doanbe.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendOtpRequest {
    private String toEmail;
}
