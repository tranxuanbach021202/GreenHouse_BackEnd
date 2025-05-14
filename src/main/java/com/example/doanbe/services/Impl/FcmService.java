package com.example.doanbe.services.Impl;

import com.example.doanbe.document.DeviceToken;
import com.example.doanbe.repository.DeviceTokenRepository;
import com.example.doanbe.services.UserDetailsImpl;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FcmService {

    @Autowired
    private final FirebaseMessaging firebaseMessaging;

    public void sendNotificationToUser(String token, String title, String body) {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setToken(token)
                .setNotification(notification)
                .build();

        System.out.println("FCM gửi ");
        try {
            String response = firebaseMessaging.send(message);
            System.out.println("FCM gửi thành công: " + response);
        } catch (FirebaseMessagingException e) {
            System.err.println("Lỗi gửi FCM: " + e.getMessage());
        }
    }

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    public void saveToken(String token, String platform) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        var now = LocalDateTime.now();
        var existing = deviceTokenRepository.findByUserIdAndToken(userDetails.getId(), token);
        if (existing.isEmpty()) {
            var deviceToken = DeviceToken.builder()
                    .userId(userDetails.getId())
                    .token(token)
                    .platform(platform)
                    .updatedAt(now)
                    .build();
            deviceTokenRepository.save(deviceToken);
        }
    }

    public void deleteToken(String userId, String token) {
        deviceTokenRepository.deleteByUserIdAndToken(userId, token);
    }
}
