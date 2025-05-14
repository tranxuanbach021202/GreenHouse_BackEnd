package com.example.doanbe.dto.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OtpVerifyRequest {
    private String email;
    private String otp;


}
