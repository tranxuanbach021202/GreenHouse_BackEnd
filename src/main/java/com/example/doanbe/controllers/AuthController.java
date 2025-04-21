package com.example.doanbe.controllers;

import com.example.doanbe.payload.request.*;
import com.example.doanbe.payload.response.VerifyOtpReponse;
import com.example.doanbe.services.AuthService;
import com.example.doanbe.services.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService mailService;


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signupRequest) {
        return authService.registerUser(signupRequest);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }


    @PostMapping("/verify-otp")
    public VerifyOtpReponse verifyOtp(@RequestBody VerifyOtpRequest otpRequest) {
        return mailService.verifyOtp(otpRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        return null;
    };

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    };

//    @PostMapping("/send-otp")
//    public String apiTest(@RequestBody SendOtpRequest sendOtpRequest) throws IOException {
//        return  emailService.sendOtpEmail(sendOtpRequest);
//    }


    @Autowired
    private EmailService emailService;




}
