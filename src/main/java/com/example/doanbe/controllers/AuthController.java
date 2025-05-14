package com.example.doanbe.controllers;

import com.example.doanbe.dto.request.OtpVerifyRequest;
import com.example.doanbe.payload.request.*;
import com.example.doanbe.payload.response.MessageResponse;
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
import java.util.Map;

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


    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    };

//    @PostMapping("/send-otp")
//    public String apiTest(@RequestBody SendOtpRequest sendOtpRequest) throws IOException {
//        return  emailService.sendOtpEmail(sendOtpRequest);
//    }


    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        authService.forgotPassword(email);
        return ResponseEntity.ok(new MessageResponse("OTP đã được gửi tới email"));
    }


    @PostMapping("/verify-otp-forgot-password")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest request) {
        String resetToken = authService.verifyOtpAndGenerateResetToken(request.getEmail(), request.getOtp());
        return ResponseEntity.ok(Map.of("resetToken", resetToken));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getResetToken(), request.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công."));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        authService.logout(token);

        return ResponseEntity.ok("Đăng xuất thành công");
    }




}
