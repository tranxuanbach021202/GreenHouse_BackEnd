package com.example.doanbe.controllers;


import com.example.doanbe.dto.request.FcmRequest;
import com.example.doanbe.services.Impl.FcmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/fcm")
public class FcmController {

    @Autowired
    private FcmService fcmService;


    @PostMapping("/device-token")
    public ResponseEntity<?> syncToken(@RequestBody Map<String, String> body) {
        var token = body.get("token");
        var platform = body.get("platform");
        fcmService.saveToken(token, platform);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody FcmRequest request) {
        fcmService.sendNotificationToUser(request.getToken(), request.getTitle(), request.getBody());
        return ResponseEntity.ok("Notification sent");
    }


}
