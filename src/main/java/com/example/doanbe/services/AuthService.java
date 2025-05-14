package com.example.doanbe.services;

import com.example.doanbe.payload.request.LoginRequest;
import com.example.doanbe.payload.request.RefreshTokenRequest;
import com.example.doanbe.payload.request.SignUpRequest;
import com.nimbusds.oauth2.sdk.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;


public interface AuthService {

    ResponseEntity<?> registerUser(SignUpRequest signupRequest);

    ResponseEntity<?> authenticateUser(LoginRequest loginRequest);

    ResponseEntity<?> logoutUser(HttpServletRequest request);

    ResponseEntity<?> forgotPassword(String username);

    String verifyOtpAndGenerateResetToken(String username, String otp);

    void resetPassword(String resetToken, String newPassword);

    ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest);

    void logout(String token);
}
