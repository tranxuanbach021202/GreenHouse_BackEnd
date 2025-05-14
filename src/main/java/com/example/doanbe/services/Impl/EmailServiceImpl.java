package com.example.doanbe.services.Impl;

import com.example.doanbe.document.User;
import com.example.doanbe.exception.AppException;
import com.example.doanbe.payload.request.VerifyOtpRequest;
import com.example.doanbe.payload.response.VerifyOtpReponse;
import com.example.doanbe.repository.UserRepository;
import com.example.doanbe.services.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

import static com.example.doanbe.config.Constants.MAX_OTP_ATTEMPTS;

@Service
public class EmailServiceImpl implements EmailService {


    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    public String generateOTP() {
        // Tạo OTP 4 số ngẫu nhiên
        Random random = new Random();
        int otp = 1000 + random.nextInt(9000);
        return String.valueOf(otp);
    }

    @Override
    public String sendOtpEmail(String toEmail){
        try {
            String testEmail = "tb520385@gmail.com";
            String otp = generateOTP();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            // Set test email thật
            helper.setTo(testEmail);
            helper.setSubject("Mã OTP Xác thực");

            String htmlContent = String.format(
                    "<div style='font-family: Arial, sans-serif; padding: 20px;'>" +
                            "<h2>Xác thực Email</h2>" +
                            "<p>Mã OTP của bạn là: <strong style='font-size: 20px;'>%s</strong></p>" +
                            "<p>Mã này sẽ hết hạn trong 5 phút.</p>" +
                            "<p>Nếu bạn không yêu cầu mã này, vui lòng bỏ qua email này.</p>" +
                            "</div>", otp);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            saveOTP(toEmail, otp);
            return "Thanh cong";
        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email OTP: " + e.getMessage());
        }

    }

    @Override
    public VerifyOtpReponse verifyOtp(VerifyOtpRequest verifyOtpRequest) {
        logger.info("OTP" + verifyOtpRequest.getOtp());
        User user = userRepository.findByEmail(verifyOtpRequest.getEmail())
                .orElseThrow(() -> new AppException(400, 51, "Lỗi: Không tìm thấy email trong database!"));
        if (user.getOneTimePassword() == null) {
            throw new AppException(400, 44, "Lỗi: Chưa có mã OTP nào được tạo");
        } else if (user.getOtpExpiryTime() != null &&
                user.getOtpExpiryTime().isBefore(LocalDateTime.now())) {
            throw new AppException(400, 44, "Lỗi: Mã OTP đã hết hạn");
        }


        if (user.getOneTimePassword().equals(verifyOtpRequest.getOtp())) {
            // Xoá Otp khỏi database
            // Update verify otp
            user.setOneTimePassword(null);
            user.setOtpExpiryTime(null);
            user.setVerified(true);
            userRepository.save(user);
            return new VerifyOtpReponse(true, "Xác thực OTP thành công");
        }
        // Đếm số lượt nhập otp
        user.setOtpAttempts(user.getOtpAttempts() + 1);
        if (user.getOtpAttempts() >= MAX_OTP_ATTEMPTS) {
            user.setOneTimePassword(null);
            user.setOtpExpiryTime(null);
            user.setOtpAttempts(0);
            userRepository.save(user);
            throw new AppException(400, 44, "Lỗi: Đã nhập quá số lần. OTP bị vô hiệu hoá");
        }
        throw new AppException(400, 44, "Lỗi: Mã OTP không chính xác");
    }


    private void  saveOTP(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(400, 51, "Lỗi: Không tìm thấy email trong database!"));
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(5);
        user.setOtp(otp);
        user.setOtpAttempts(0);
        userRepository.save(user);
    }
}
