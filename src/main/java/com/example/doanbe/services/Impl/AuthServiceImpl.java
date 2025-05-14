package com.example.doanbe.services.Impl;

import com.example.doanbe.dto.ApiErrorResponseDto;
import com.example.doanbe.enums.ERole;
import com.example.doanbe.document.RefreshToken;
import com.example.doanbe.exception.AppException;
import com.example.doanbe.document.Role;
import com.example.doanbe.document.User;
import com.example.doanbe.exception.TokenRefreshException;
import com.example.doanbe.payload.request.LoginRequest;
import com.example.doanbe.payload.request.RefreshTokenRequest;
import com.example.doanbe.payload.request.SignUpRequest;
import com.example.doanbe.payload.response.JwtResponse;
import com.example.doanbe.payload.response.MessageResponse;
import com.example.doanbe.payload.response.TokenRefreshResponse;
import com.example.doanbe.repository.RefreshTokenRepository;
import com.example.doanbe.repository.RoleRepository;
import com.example.doanbe.repository.UserRepository;
import com.example.doanbe.security.jwt.JwtUtils;
import com.example.doanbe.services.AuthService;
import com.example.doanbe.services.EmailService;
import com.example.doanbe.services.RefreshTokenService;
import com.example.doanbe.services.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private EmailService emailService;

    @Autowired
    RefreshTokenService refreshTokenService;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);


    @Override
    public ResponseEntity<?> registerUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new AppException(400, 5, "Lỗi: Username này đã được đăng ký trước đó!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new AppException(400, 6, "Lỗi: Email này đã được đăng ký trước đó!");
        }

        // Tạo tài khoản mới
        User user = new User(
                signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword())
        );

        user.setCreatedAt(LocalDateTime.now());


        Set<String> strRoles = signUpRequest.getRoles(); // Giả sử đã đổi tên thành getRoles()
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new AppException(500, 51, "Lỗi: Không tìm thấy vai trò người dùng!"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                if ("ADMIN".equals(role)) {
                    Role userRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow(
                                    () -> new AppException(500, 51, "Lỗi: Không tìm thấy vai trò quản trị viên!"));
                    roles.add(userRole);
                } else {
                    Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow(
                                    () -> new AppException(500, 51, "Lỗi: Không tìm thấy vai trò người dùng!"));
                    roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        emailService.sendOtpEmail(signUpRequest.getEmail());
        return ResponseEntity.ok(new MessageResponse("Đăng ký tài khoản thành công!. Vui lòng check" +
                "Email để xác thực tài khoản"));

    }


    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        User fromDB = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (fromDB == null) {
            throw new AppException(404, 44,
                    "Lỗi: Không tồn tại! Không tìm thấy tài khoản có tên '" + loginRequest.getUsername()
                            + "'!");
        } else if (!fromDB.isVerified()) {
            throw new AppException(403, 43,
                    "Tài khoản của bạn chưa được quản trị viên phê duyệt, vui lòng đợi!");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        String role = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .findFirst()
                .orElse("ROLE_USER");
        return ResponseEntity.ok(
                new JwtResponse(jwt,
                        refreshToken.getToken(),
                        userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail(),
                        role)
        );
    }

    @Override
    public ResponseEntity<?> logoutUser(HttpServletRequest request) {
        try {
            String jwt = extractJwtFromRequest(request);
            if (jwt == null) {
                return ResponseEntity.badRequest()
                        .body(new ApiErrorResponseDto(400, "Không tìm thấy token"));
            }
            return ResponseEntity.ok("Đăng xuất thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiErrorResponseDto(501, "Lỗi trong quá trình đăng xuất: " + e.getMessage()));
        }

    }



    @Override
    public ResponseEntity<?> forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(404, 44, "Không tìm thấy người dùng"));
        emailService.sendOtpEmail(user.getEmail());
        return ResponseEntity.ok("Đã gửi OTP tới email của bạn");
    }


    @Override
    public String verifyOtpAndGenerateResetToken(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(404, 1, "Không tìm thấy người dùng"));

        if (!otp.equals(user.getOneTimePassword()) || user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException(400, 2, "OTP không hợp lệ hoặc đã hết hạn");
        }

        // Tạo JWT resetToken
        String resetToken = jwtUtils.generateResetToken(user.getUsername());

        return resetToken;
    }

//    public void resetPassword(String resetToken, String newPassword) {
//        String username = jwtUtils.getUserNameFromJwtToken(resetToken);
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new AppException(404, 1, "Không tìm thấy người dùng"));
//
//        user.setPassword(passwordEncoder.encode(newPassword));
//        user.setOneTimePassword(null);
//        user.setOtpExpiryTime(null);
//        userRepository.save(user);
//    }



    @Override
    public void resetPassword(String resetToken, String newPassword) {
        if (!jwtUtils.validateJwtToken(resetToken)) {
            throw new AppException(401, 10, "Token không hợp lệ hoặc đã hết hạn");
        }

        String username = jwtUtils.getUserNameFromJwtToken(resetToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(404, 11, "Không tìm thấy người dùng"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());

        user.setOneTimePassword(null);
        user.setOtpExpiryTime(null);

        userRepository.save(user);
    }



    @Override
    public ResponseEntity<?> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String requestRefreshToken = refreshTokenRequest.getRefreshToken();
        logger.info("Refresh token: {}", requestRefreshToken);
        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpirytion)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


    private String generateOTP() {
        return String.format("%04d", new Random().nextInt(9999));
    }

    @Override
    public void logout(String token) {
        String username = jwtUtils.getUserNameFromJwtToken(token);
        refreshTokenRepository.deleteByUserId(username);
    }
    
}
