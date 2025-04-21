package com.example.doanbe.services;

import com.example.doanbe.payload.request.VerifyOtpRequest;
import com.example.doanbe.payload.response.VerifyOtpReponse;

public interface EmailService {

    String sendOtpEmail(String toEmail);
    VerifyOtpReponse verifyOtp(VerifyOtpRequest verifyOtpRequest);

}
