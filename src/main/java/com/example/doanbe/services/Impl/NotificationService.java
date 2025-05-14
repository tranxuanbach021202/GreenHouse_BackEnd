package com.example.doanbe.services.Impl;

import com.example.doanbe.document.Invitation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FcmService fcmService;

    public void sendInvitation(Invitation inv) {
        String title = "Lời mời tham gia dự án";
        String body = String.format("Bạn được mời tham gia dự án %s bởi %s", inv.getProjectName(), inv.getInviterName());

        String token = getDeviceToken(inv.getInvitedUserId());
        if (token != null) {
            fcmService.sendNotificationToUser(token, title, body);
        }
    }

    private String getDeviceToken(String userId) {
        return "device_token_test";
    }
}
