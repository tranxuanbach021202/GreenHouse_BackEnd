package com.example.doanbe.services;

import com.example.doanbe.payload.request.LoginRequest;
import com.example.doanbe.payload.request.RefreshTokenRequest;
import com.example.doanbe.payload.request.SignUpRequest;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;


public interface AuthService {
    SuccessResponse authenticate(LoginRequest loginRequest);

    ResponseEntity<?> registerUser(SignUpRequest signupRequest);

    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);

    ResponseEntity<?> logoutUser(HttpServletRequest request);

    ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest);
}
